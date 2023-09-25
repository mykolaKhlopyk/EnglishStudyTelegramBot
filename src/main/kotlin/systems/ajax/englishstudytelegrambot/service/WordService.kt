package systems.ajax.englishstudytelegrambot.service

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
        val libraryId = libraryRepository.getLibraryIdByPairLibraryNameAndTelegramUserId(libraryName, telegramUserId)
        libraryId.takeIf {
            !wordRepository.isWordBelongsToLibraryByWordSpelling(wordDto.spelling, libraryId)
        }?.let {
            return wordRepository.saveNewWord(
                Word(
                    spelling = wordDto.spelling,
                    translate = wordDto.translate,
                    libraryId = libraryId,
                    additionalInfoAboutWord = additionalInfoAboutWordService.findAdditionInfoAboutWord(wordDto.spelling)
                )
            )
        } ?: throw WordAlreadyPresentInLibraryException()
    }

    override fun updateWordTranslate(libraryName: String, telegramUserId: String, wordDto: WordDto): Word {
        val libraryId = libraryRepository.getLibraryIdByPairLibraryNameAndTelegramUserId(libraryName, telegramUserId)
        val wordId = wordRepository.getWordIdBySpellingAndLibraryId(wordDto.spelling, libraryId)
        wordId.takeIf {
            wordRepository.isWordBelongsToLibraryByWordId(wordId, libraryId)
        }?.let {
            return wordRepository.updateWordTranslating(wordId, wordDto.translate)
        } ?: throw WordNotFoundBySpendingException()
    }

    override fun deleteWord(libraryName: String, telegramUserId: String, wordSpelling: String): Word {
        val libraryId = libraryRepository.getLibraryIdByPairLibraryNameAndTelegramUserId(libraryName, telegramUserId)
        val wordId = wordRepository.getWordIdBySpellingAndLibraryId(libraryName, telegramUserId)
        wordId.takeIf {
            wordRepository.isWordBelongsToLibraryByWordId(wordId, libraryId)
        }?.let {
            return wordRepository.deleteWord(wordSpelling)
        } ?: throw WordNotFoundBySpendingException()
    }

    override fun getFullInfoAboutWord(libraryName: String, telegramUserId: String, wordSpelling: String): Word {
        val libraryId = libraryRepository.getLibraryIdByPairLibraryNameAndTelegramUserId(libraryName, telegramUserId)
        val wordId = wordRepository.getWordIdBySpellingAndLibraryId(wordSpelling, libraryId)
        return wordRepository.getFullInfoAboutWord(wordId)
    }
}
