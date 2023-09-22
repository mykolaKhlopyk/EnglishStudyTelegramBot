package systems.ajax.englishstudytelegrambot.service

import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.User
import systems.ajax.englishstudytelegrambot.exception.LibraryAlreadyPresentExceptions
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.UserRepository

interface UserService {

    fun getAllLibrariesOfUser(telegramIdOfUser: String): List<Library>
}

@Service
class UserServiceImpl(
    val userRepository: UserRepository,
    val libraryRepository: LibraryRepository
) : UserService {

    override fun getAllLibrariesOfUser(telegramIdOfUser: String): List<Library> =
        userRepository.getAllLibrariesOfUser(telegramIdOfUser)
}
