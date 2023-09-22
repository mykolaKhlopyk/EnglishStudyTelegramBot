package systems.ajax.englishstudytelegrambot.service

import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.User
import systems.ajax.englishstudytelegrambot.exception.LibraryAlreadyPresentExceptions
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.UserRepository

interface LibraryService {
    fun createNewLibrary(nameOfNewLibrary: String, telegramUserId: String): Library
    fun deleteLibrary(nameOfLibraryForDeleting: String): Library

}

@Service
class LibraryServiceImpl(
    val libraryRepository: LibraryRepository,
    val userRepository: UserRepository
) : LibraryService {
    override fun createNewLibrary(nameOfNewLibrary: String, telegramUserId: String): Library {
        val user = userRepository.getUserByTelegramId(telegramUserId)
        val createdLibrary: Library = libraryRepository.takeIf {
            !user.isHavingLibraryWithName(nameOfNewLibrary)
        }?.saveNewLibrary(nameOfNewLibrary, user.telegramUserId)
            ?: throw LibraryAlreadyPresentExceptions()
        return createdLibrary
    }

    private fun User.isHavingLibraryWithName(nameOfNewLibrary: String) =
        !userRepository.getAllLibrariesOfUser(telegramUserId)
            .asSequence()
            .map(Library::name)
            .contains(nameOfNewLibrary)
}
