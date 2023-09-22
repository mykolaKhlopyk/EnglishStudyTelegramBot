package systems.ajax.englishstudytelegrambot.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.dto.WordDto
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.WordRepository

interface WordService {

    fun getWordByItsSpelling(wordSpelling: String): Word

    suspend fun saveNewWordInLibrary(libraryName: String, telegramUserId: String, wordDto: WordDto): Library
}

@Service
class WordServiceImpl(
    val wordRepository: WordRepository
    val additionalInfoAboutWordService: AdditionalInfoAboutWordService
) : WordService {

    override suspend fun saveNewWordInLibrary(libraryName: String, telegramUserId: String, wordDto: WordDto): Library {
        val word =
            Word(
                wordDto.spelling,
                wordDto.translate,
                additionalInfoAboutWordService.findAdditionInfoAboutWord(wordDto.spelling)
            )
        return wordRepository.saveNewWordInLibrary(word, libraryName, telegramUserId)
    }

    override fun getWordByItsSpelling(wordSpelling: String): Word =
        wordRepository.findById(wordSpelling)

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
