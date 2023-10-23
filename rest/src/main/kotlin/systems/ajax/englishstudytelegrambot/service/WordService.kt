package systems.ajax.englishstudytelegrambot.service

import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
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
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

interface WordService {

    fun saveNewWord(
        libraryName: String,
        telegramUserId: String,
        createWordDtoRequest: CreateWordDtoRequest
    ): Mono<WordDtoResponse>

    fun updateWordTranslate(
        libraryName: String,
        telegramUserId: String,
        createWordDtoRequest: CreateWordDtoRequest
    ): Mono<WordDtoResponse>

    fun deleteWord(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): Mono<WordDtoResponse>

    fun getFullInfoAboutWord(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): Mono<WordDtoResponse>
}

@Service
class WordServiceImpl(
    val wordRepository: WordRepository,
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
        .getWordIdByLibraryNameTelegramUserIdWordSpelling(
            libraryName,
            telegramUserId,
            createWordDtoRequest.spelling
        )
        .switchIfEmpty(checkWordOrLibraryIsMissing(libraryName, telegramUserId))
        .flatMap { wordId -> wordRepository.updateWordTranslating(wordId, createWordDtoRequest.translate) }
        .map(Word::toDtoResponse)

    override fun deleteWord(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): Mono<WordDtoResponse> = wordRepository
        .getWordIdByLibraryNameTelegramUserIdWordSpelling(
            libraryName,
            telegramUserId,
            wordSpelling
        )
        .switchIfEmpty(checkWordOrLibraryIsMissing(libraryName, telegramUserId))
        .doOnNext { id -> log.info("id of deleted word is {}", id) }
        .flatMap(wordRepository::deleteWord)
        .map(Word::toDtoResponse)

    override fun getFullInfoAboutWord(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): Mono<WordDtoResponse> = wordRepository
        .getWordByLibraryNameTelegramUserIdWordSpelling(libraryName, telegramUserId, wordSpelling)
        .onErrorMap { WordIsMissingException() }
        .map(Word::toDtoResponse)

    private fun findLibraryIdWithoutCertainSpelling(
        libraryName: String,
        telegramUserId: String,
        spelling: String
    ): Mono<ObjectId> = findLibraryId(libraryName, telegramUserId)
        .filterWhen { libraryId -> isWordNotBelongsToLibrary(libraryId, spelling) }
        .switchIfEmpty(Mono.error(WordAlreadyPresentInLibraryException()))

    private fun findLibraryId(
        libraryName: String,
        telegramUserId: String,
    ): Mono<ObjectId> = libraryRepository
        .getLibraryIdByLibraryNameAndTelegramUserId(
            libraryName,
            telegramUserId
        ).onErrorMap { LibraryIsMissingException() }

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

    private fun checkWordOrLibraryIsMissing(
        libraryName: String,
        telegramUserId: String
    ): Mono<ObjectId> = findLibraryId(libraryName, telegramUserId)
        .switchIfEmpty(Mono.error(LibraryIsMissingException()))
        .flatMap { Mono.error(WordIsMissingException()) }

    private fun isWordNotBelongsToLibrary(
        libraryId: ObjectId,
        wordSpelling: String
    ): Mono<Boolean> = wordRepository
        .isWordBelongsToLibrary(
            wordSpelling,
            libraryId
        )
        .map { isBelong -> !isBelong }

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
