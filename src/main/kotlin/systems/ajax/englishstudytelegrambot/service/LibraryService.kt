package systems.ajax.englishstudytelegrambot.service

import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.User
import systems.ajax.englishstudytelegrambot.exception.LibraryAlreadyPresentExceptions
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.UserRepository

interface LibraryService {
    fun createNewLibrary(nameOfNewLibrary: String, telegramIdOfUser: String): Library

}

@Service
class LibraryServiceImpl(
    val libraryRepository: LibraryRepository,
    val userRepository: UserRepository
) : LibraryService {
    override fun createNewLibrary(nameOfNewLibrary: String, telegramIdOfUser: String): Library {
        val user = userRepository.getUserByTelegramId(telegramIdOfUser)
        val createdLibrary: Library = libraryRepository.takeIf {
            user.doesntHaveLibrariesWithName(nameOfNewLibrary)
        }?.saveNewLibrary(nameOfNewLibrary, user.telegramId)
            ?: throw LibraryAlreadyPresentExceptions()
        return createdLibrary
    }

    private fun User.doesntHaveLibrariesWithName(nameOfNewLibrary: String) =
        !userRepository.getAllLibrariesOfUser(telegramId)
            .asSequence()
            .map(Library::name)
            .contains(nameOfNewLibrary)
}
