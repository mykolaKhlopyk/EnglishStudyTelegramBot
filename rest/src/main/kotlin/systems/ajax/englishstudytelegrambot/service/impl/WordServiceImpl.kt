package systems.ajax.englishstudytelegrambot.service.impl

import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import systems.ajax.englishstudytelegrambot.dto.entity.WordDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.toDtoResponse
import systems.ajax.englishstudytelegrambot.dto.request.CreateWordDtoRequest
import systems.ajax.englishstudytelegrambot.entity.AdditionalInfoAboutWord
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.exception.LibraryIsMissingException
import systems.ajax.englishstudytelegrambot.exception.WordAlreadyPresentInLibraryException
import systems.ajax.englishstudytelegrambot.exception.WordIsMissingException
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.WordRepository
import systems.ajax.englishstudytelegrambot.service.AdditionalInfoAboutWordService
import systems.ajax.englishstudytelegrambot.service.WordService


@Service
class WordServiceImpl(
    @Qualifier("wordCashableRepositoryImpl") val wordRepository: WordRepository,
    val libraryRepository: LibraryRepository,
    val additionalInfoAboutWordService: AdditionalInfoAboutWordService
) : WordService {

    override fun saveNewWord(
        libraryName: String,
        telegramUserId: String,
        createWordDtoRequest: CreateWordDtoRequest
    ): Mono<WordDtoResponse> =
        Mono.zip(
            findLibraryIdWithoutCertainSpelling(libraryName, telegramUserId, createWordDtoRequest.spelling),
            additionalInfoAboutWordService.findAdditionInfoAboutWord(createWordDtoRequest.spelling)
        ).flatMap { (libraryId, additionalInfoAboutWord) ->
            collectAndSaveWordInDb(createWordDtoRequest, libraryId, additionalInfoAboutWord)
        }.map(Word::toDtoResponse)

    override fun updateWordTranslate(
        libraryName: String,
        telegramUserId: String,
        createWordDtoRequest: CreateWordDtoRequest
    ): Mono<WordDtoResponse> = wordRepository
        .getWordByLibraryNameTelegramUserIdWordSpelling(
            libraryName,
            telegramUserId,
            createWordDtoRequest.spelling
        )
        .switchIfEmpty(
            findLibraryId(libraryName, telegramUserId).handle { _, sink ->
                sink.error(WordIsMissingException("word is missing in library"))
            }
        )
        .flatMap { word -> wordRepository.updateWordTranslating(word.id, createWordDtoRequest.translate) }
        .map(Word::toDtoResponse)

    override fun deleteWord(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): Mono<WordDtoResponse> = wordRepository
        .getWordByLibraryNameTelegramUserIdWordSpelling(
            libraryName,
            telegramUserId,
            wordSpelling
        )
        .switchIfEmpty(
            findLibraryId(libraryName, telegramUserId)
                .handle { _, sink ->
                    sink.error(WordIsMissingException("spelling = $wordSpelling"))
                }
        )
        .doOnNext { id -> log.info("id of deleted word is {}", id) }
        .map(Word::id)
        .flatMap(wordRepository::deleteWord)
        .map(Word::toDtoResponse)

    override fun getFullInfoAboutWord(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): Mono<WordDtoResponse> = wordRepository
        .getWordByLibraryNameTelegramUserIdWordSpelling(libraryName, telegramUserId, wordSpelling)
        .switchIfEmpty(
            findLibraryId(libraryName, telegramUserId)
                .handle { _, sink ->
                    sink.error(WordIsMissingException("spelling = $wordSpelling"))
                }
        )
        .map(Word::toDtoResponse)

    private fun findLibraryIdWithoutCertainSpelling(
        libraryName: String,
        telegramUserId: String,
        spelling: String
    ): Mono<ObjectId> = findLibraryId(libraryName, telegramUserId)
        .doOnNext{ log.info("library id = {}", it)}
        // .doOnNext{ log.info("is word not belongs = {}", isWordNotBelongsToLibrary(it, spelling).block()!!)}
        .filterWhen {
                libraryId -> isWordNotBelongsToLibrary(libraryId, spelling)
        }
        .switchIfEmpty(
            Mono.error(WordAlreadyPresentInLibraryException("word $spelling is present in library $libraryName"))
        )


    private fun findLibraryId(
        libraryName: String,
        telegramUserId: String
    ): Mono<ObjectId> = libraryRepository
        .getLibraryIdByLibraryNameAndTelegramUserId(
            libraryName,
            telegramUserId
        )
        .switchIfEmpty(
            Mono.error(LibraryIsMissingException("libraryName = $libraryName and telegramUserId = $telegramUserId"))
        )

    private fun collectAndSaveWordInDb(
        createWordDtoRequest: CreateWordDtoRequest,
        libraryId: ObjectId,
        additionalInfoAboutWord: AdditionalInfoAboutWord
    ): Mono<Word> = wordRepository
        .saveNewWord(
            Word(
                spelling = createWordDtoRequest.spelling,
                translate = createWordDtoRequest.translate,
                libraryId = libraryId,
                additionalInfoAboutWord = additionalInfoAboutWord
            )
        )

    private fun isWordNotBelongsToLibrary(
        libraryId: ObjectId,
        wordSpelling: String
    ): Mono<Boolean> = wordRepository
        .isWordBelongsToLibrary(
            wordSpelling,
            libraryId
        ).map {
            it.not()
        }

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
