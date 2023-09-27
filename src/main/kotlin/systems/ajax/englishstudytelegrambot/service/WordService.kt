package systems.ajax.englishstudytelegrambot.service

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.dto.WordDto
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.exception.WordAlreadyPresentInLibraryException
import systems.ajax.englishstudytelegrambot.exception.WordNotFoundBySpendingException
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.WordRepository

interface WordService {

    suspend fun saveNewWord(libraryName: String, telegramUserId: String, wordDto: WordDto): Word

    fun updateWordTranslate(libraryName: String, telegramUserId: String, wordDto: WordDto): Word

    fun deleteWord(libraryName: String, telegramUserId: String, wordSpelling: String): Word

    fun getFullInfoAboutWord(libraryName: String, telegramUserId: String, wordSpelling: String): Word
}

@Service
class WordServiceImpl(
    val wordRepository: WordRepository,
    val libraryRepository: LibraryRepository,
    val additionalInfoAboutWordService: AdditionalInfoAboutWordService
) : WordService {

    override suspend fun saveNewWord(libraryName: String, telegramUserId: String, wordDto: WordDto): Word {
        val libraryId = libraryRepository.getLibraryIdByLibraryNameAndTelegramUserId(libraryName, telegramUserId)
        if (wordRepository.isWordBelongsToLibraryByWordSpelling(wordDto.spelling, libraryId))
            throw WordAlreadyPresentInLibraryException()

        val additionalInfo = additionalInfoAboutWordService.findAdditionInfoAboutWord(wordDto.spelling)
        return wordRepository.saveNewWord(
            Word(
                spelling = wordDto.spelling,
                translate = wordDto.translate,
                libraryId = libraryId,
                additionalInfoAboutWord = additionalInfo
            )
        )
    }

    override fun updateWordTranslate(libraryName: String, telegramUserId: String, wordDto: WordDto): Word {
        val wordId = wordRepository.getWordIdByLibraryNameTelegramUserIdWordSpelling(
            libraryName,
            telegramUserId,
            wordDto.spelling
        )
        return wordRepository.updateWordTranslating(wordId, wordDto.translate)
    }

    override fun deleteWord(libraryName: String, telegramUserId: String, wordSpelling: String): Word {
        val wordId =
            wordRepository.getWordIdByLibraryNameTelegramUserIdWordSpelling(libraryName, telegramUserId, wordSpelling)
        return wordRepository.deleteWord(wordId)
    }

    override fun getFullInfoAboutWord(libraryName: String, telegramUserId: String, wordSpelling: String): Word =
        wordRepository.getWordByLibraryNameTelegramUserIdWordSpelling(libraryName, telegramUserId, wordSpelling)
}
