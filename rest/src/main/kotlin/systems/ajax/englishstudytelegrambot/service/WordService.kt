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
import systems.ajax.englishstudytelegrambot.exception.WordAlreadyPresentInLibraryException
import systems.ajax.englishstudytelegrambot.exception.WordIsMissing
import systems.ajax.englishstudytelegrambot.exception.WordNotFoundBySpendingException
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.WordRepository

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
        Mono
            .zip(
                findLibraryId(libraryName, telegramUserId, createWordDtoRequest),
                additionalInfoAboutWordService.findAdditionInfoAboutWord(createWordDtoRequest.spelling)
            )
            .flatMap { t -> collectAndSaveWordInDb(createWordDtoRequest, t.t1, t.t2) }
            .map(Word::toDtoResponse)

    override fun updateWordTranslate(
        libraryName: String,
        telegramUserId: String,
        createWordDtoRequest: CreateWordDtoRequest
    ): Mono<WordDtoResponse> =
        wordRepository
            .getWordIdByLibraryNameTelegramUserIdWordSpelling(
                libraryName,
                telegramUserId,
                createWordDtoRequest.spelling
            )
            .flatMap { wordId ->
                wordRepository.updateWordTranslating(wordId, createWordDtoRequest.translate)
            }
            .onErrorMap { WordNotFoundBySpendingException() }
            .map(Word::toDtoResponse)

    override fun deleteWord(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): Mono<WordDtoResponse> =
        wordRepository
            .getWordIdByLibraryNameTelegramUserIdWordSpelling(
                libraryName,
                telegramUserId,
                wordSpelling
            )
            .flatMap(wordRepository::deleteWord)
            .onErrorMap{WordNotFoundBySpendingException()}
            .map(Word::toDtoResponse)

    override fun getFullInfoAboutWord(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): Mono<WordDtoResponse> =
        wordRepository
            .getWordByLibraryNameTelegramUserIdWordSpelling(libraryName, telegramUserId, wordSpelling)
            .onErrorMap { WordIsMissing() }
            .map(Word::toDtoResponse)

    private fun findLibraryId(
        libraryName: String,
        telegramUserId: String,
        createWordDtoRequest: CreateWordDtoRequest
    ): Mono<ObjectId> =
        libraryRepository
            .getLibraryIdByLibraryNameAndTelegramUserId(
                libraryName,
                telegramUserId
            )
            .filterWhen { libraryId -> isWordNotBelongsToLibrary(libraryId, createWordDtoRequest.spelling) }
            .switchIfEmpty(Mono.error(WordAlreadyPresentInLibraryException()))

    private fun collectAndSaveWordInDb(
        createWordDtoRequest: CreateWordDtoRequest,
        libraryId: ObjectId,
        additionalInfoAboutWord: AdditionalInfoAboutWord
    ): Mono<Word> =
        wordRepository
            .saveNewWord(
                Word(
                    spelling = createWordDtoRequest.spelling,
                    translate = createWordDtoRequest.translate,
                    libraryId = libraryId,
                    additionalInfoAboutWord = additionalInfoAboutWord
                )
            )

    private fun isWordNotBelongsToLibrary(libraryId: ObjectId, wordSpelling: String): Mono<Boolean> =
        wordRepository
            .isWordBelongsToLibrary(
                wordSpelling,
                libraryId
            ).map { isBelong -> !isBelong }
}
