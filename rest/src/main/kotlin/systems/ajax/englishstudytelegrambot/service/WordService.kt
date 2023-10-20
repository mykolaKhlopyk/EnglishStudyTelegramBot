package systems.ajax.englishstudytelegrambot.service

import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.dto.entity.WordDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.toDtoResponse
import systems.ajax.englishstudytelegrambot.dto.request.CreateWordDtoRequest
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.exception.WordAlreadyPresentInLibraryException
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.WordRepository

interface WordService {

    suspend fun saveNewWord(
        libraryName: String,
        telegramUserId: String,
        createWordDtoRequest: CreateWordDtoRequest
    ): WordDtoResponse

    fun updateWordTranslate(
        libraryName: String,
        telegramUserId: String,
        createWordDtoRequest: CreateWordDtoRequest
    ): WordDtoResponse

    fun deleteWord(libraryName: String, telegramUserId: String, wordSpelling: String): WordDtoResponse

    fun getFullInfoAboutWord(libraryName: String, telegramUserId: String, wordSpelling: String): WordDtoResponse
}

@Service
class WordServiceImpl(
    val wordRepository: WordRepository,
    val libraryRepository: LibraryRepository,
    val additionalInfoAboutWordService: AdditionalInfoAboutWordService
) : WordService {

    override suspend fun saveNewWord(
        libraryName: String,
        telegramUserId: String,
        createWordDtoRequest: CreateWordDtoRequest
    ): WordDtoResponse {
        val libraryId = libraryRepository.getLibraryIdByLibraryNameAndTelegramUserId(libraryName, telegramUserId)
        if (wordRepository.isWordBelongsToLibraryByWordSpelling(createWordDtoRequest.spelling, libraryId))
            throw WordAlreadyPresentInLibraryException()

        val additionalInfo = additionalInfoAboutWordService.findAdditionInfoAboutWord(createWordDtoRequest.spelling)
        return wordRepository.saveNewWord(
            Word(
                spelling = createWordDtoRequest.spelling,
                translate = createWordDtoRequest.translate,
                libraryId = libraryId,
                additionalInfoAboutWord = additionalInfo
            )
        ).toDtoResponse()
    }

    override fun updateWordTranslate(
        libraryName: String,
        telegramUserId: String,
        createWordDtoRequest: CreateWordDtoRequest
    ): WordDtoResponse {
        val wordId = wordRepository.getWordIdByLibraryNameTelegramUserIdWordSpelling(
            libraryName,
            telegramUserId,
            createWordDtoRequest.spelling
        )
        return wordRepository.updateWordTranslating(wordId, createWordDtoRequest.translate)
            .toDtoResponse()
    }

    override fun deleteWord(libraryName: String, telegramUserId: String, wordSpelling: String): WordDtoResponse {
        val wordId =
            wordRepository.getWordIdByLibraryNameTelegramUserIdWordSpelling(libraryName, telegramUserId, wordSpelling)
        return wordRepository.deleteWord(wordId)
            .toDtoResponse()
    }

    override fun getFullInfoAboutWord(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): WordDtoResponse =
        wordRepository.getWordByLibraryNameTelegramUserIdWordSpelling(libraryName, telegramUserId, wordSpelling)
            .toDtoResponse()
}
