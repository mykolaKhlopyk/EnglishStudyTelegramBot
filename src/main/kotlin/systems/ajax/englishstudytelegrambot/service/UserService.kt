package systems.ajax.englishstudytelegrambot.service

import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.UserRepository

interface UserService {

    fun getAllLibrariesOfUser(telegramUserId: String): List<Library>
}

@Service
class UserServiceImpl(
    val userRepository: UserRepository,
) : UserService {

    override fun getAllLibrariesOfUser(telegramUserId: String): List<Library> =
        userRepository.getAllLibrariesOfUser(telegramUserId)
}
