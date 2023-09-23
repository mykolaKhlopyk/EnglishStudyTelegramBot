package systems.ajax.englishstudytelegrambot.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.dto.WordDto
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.exception.WordAlreadyPresentInLibraryException
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.WordRepository

interface WordService {

    suspend fun saveNewWordInLibrary(libraryName: String, telegramUserId: String, wordDto: WordDto): Library
}

@Service
class WordServiceImpl(
    val wordRepository: WordRepository,
    val libraryRepository: LibraryRepository,
    val additionalInfoAboutWordService: AdditionalInfoAboutWordService
) : WordService {

    override suspend fun saveNewWordInLibrary(libraryName: String, telegramUserId: String, wordDto: WordDto): Library {
        val library = libraryRepository.getLibraryByPairLibraryNameAndTelegramUserId(libraryName, telegramUserId)
        library
            .takeIf { isLibraryContainsWordWithSpelling(it, wordDto.spelling) }
            ?.let {
                val word =
                    Word(
                        wordDto.spelling,
                        wordDto.translate,
                        additionalInfoAboutWordService.findAdditionInfoAboutWord(wordDto.spelling)
                    )
                return wordRepository.saveNewWordInLibrary(word, it)
            } ?: throw WordAlreadyPresentInLibraryException()
    }


    private fun isLibraryContainsWordWithSpelling(
        library: Library,
        wordSpelling: String
    ) = library.words.none { word -> word.spelling == wordSpelling }

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
