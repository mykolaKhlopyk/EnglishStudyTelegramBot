package systems.ajax.englishstudytelegrambot.service

import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.entity.MongoLibrary
import systems.ajax.englishstudytelegrambot.repository.UserRepository

interface UserService {

    fun getAllLibrariesOfUser(telegramUserId: String): List<MongoLibrary>
}

@Service
class UserServiceImpl(
    val userRepository: UserRepository,
) : UserService {

    override fun getAllLibrariesOfUser(telegramUserId: String): List<MongoLibrary> =
        userRepository.getAllLibrariesOfUser(telegramUserId)
}
