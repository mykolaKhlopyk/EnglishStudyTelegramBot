package systems.ajax.englishstudytelegrambot.service

import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.entity.MongoLibrary
import systems.ajax.englishstudytelegrambot.entity.MongoWord
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository

interface LibraryService {

    fun createNewLibrary(nameOfNewLibrary: String, telegramUserId: String): MongoLibrary

    fun deleteLibrary(nameOfLibraryForDeleting: String, telegramUserId: String): MongoLibrary

    fun getAllWordsFromLibrary(libraryName: String, telegramUserId: String): List<MongoWord>
}

@Service
class LibraryServiceImpl(
    val libraryRepository: LibraryRepository
) : LibraryService {

    override fun createNewLibrary(nameOfNewLibrary: String, telegramUserId: String): MongoLibrary =
        libraryRepository.saveNewLibrary(nameOfNewLibrary, telegramUserId)

    override fun deleteLibrary(nameOfLibraryForDeleting: String, telegramUserId: String): MongoLibrary {
        val libraryId =
            libraryRepository.getLibraryIdByLibraryNameAndTelegramUserId(nameOfLibraryForDeleting, telegramUserId)
        return libraryRepository.deleteLibrary(libraryId)
    }

    override fun getAllWordsFromLibrary(libraryName: String, telegramUserId: String): List<MongoWord> {
        val libraryId = libraryRepository.getLibraryIdByLibraryNameAndTelegramUserId(libraryName, telegramUserId)
        return libraryRepository.getAllWordsFromLibrary(libraryId)
    }
}
