package systems.ajax.englishstudytelegrambot.service

import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.entity.User
import systems.ajax.englishstudytelegrambot.repository.UserRepository
import systems.ajax.englishstudytelegrambot.telegram.TelegramBot

interface UserService {

    fun getAll(): List<User>

    fun saveUser(): User

    fun getUserByTelegramId(telegramId: String): User?
}

@Service
class UserServiceImpl(
    val userRepository: UserRepository,
    val telegramBot: TelegramBot
) : UserService {

    override fun getAll(): List<User> = userRepository.getAll()

    override fun saveUser(): User =
        getUserByTelegramId(telegramBot.userId) ?: userRepository.insert(User(telegramBot.userId))

    override fun getUserByTelegramId(telegramId: String): User? =
        userRepository.getUserByTelegramId(telegramId)
}
