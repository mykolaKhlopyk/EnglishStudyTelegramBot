package systems.ajax.englishstudytelegrambot.service

import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.UserRepository

interface LibraryService {
    fun createNewLibrary(nameOfNewLibrary: String, telegramUserId: String): Library
    fun deleteLibrary(nameOfLibraryForDeleting: String, telegramUserId: String): Library

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

    private fun String.isUserHavingLibraryWithName(nameOfNewLibrary: String) =
        userRepository.getAllLibrariesOfUser(this)
            .asSequence()
            .map(Library::name)
            .contains(nameOfNewLibrary)
}
