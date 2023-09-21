package systems.ajax.englishstudytelegrambot.service

import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.entity.User
import systems.ajax.englishstudytelegrambot.repository.UserRepository

interface AdminService{

    fun getAll(): List<User>

    fun saveUser(): User

    fun getUserByTelegramId(): User?

}

@Service
class AdminServiceImpl(
    val userRepository: UserRepository,
    val userService: UserService
) : AdminService{
    override fun getAll(): List<User> = userRepository.getAll()

    override fun saveUser(): User =
        getUserByTelegramId() ?: userRepository.insert(User(userService.telegramIdOfCurrentUser))

    override fun getUserByTelegramId(): User? =
        userRepository.getUserByTelegramId(userService.telegramIdOfCurrentUser)
}
