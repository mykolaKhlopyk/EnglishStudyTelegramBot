package systems.ajax.englishstudytelegrambot.service

import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.UserRepository

interface LibraryService {

    fun createNewLibrary(nameOfNewLibrary: String, telegramUserId: String): Library

    fun deleteLibrary(nameOfLibraryForDeleting: String, telegramUserId: String): Library

    fun getAllWordsFromLibrary(libraryName: String, telegramUserId: String): List<Word>
}

@Service
class LibraryServiceImpl(
    val libraryRepository: LibraryRepository,
    val userRepository: UserRepository
) : LibraryService {

    override fun createNewLibrary(nameOfNewLibrary: String, telegramUserId: String): Library =
        libraryRepository.saveNewLibrary(nameOfNewLibrary, telegramUserId)


    override fun deleteLibrary(nameOfLibraryForDeleting: String, telegramUserId: String): Library =
        libraryRepository.deleteLibrary(nameOfLibraryForDeleting, telegramUserId)

    override fun getAllWordsFromLibrary(libraryName: String, telegramUserId: String): List<Word> {
        val libraryId = libraryRepository.getLibraryIdByPairLibraryNameAndTelegramUserId(libraryName, telegramUserId)
        return libraryRepository.getAllWordsFromLibrary(libraryId)
    }
}
