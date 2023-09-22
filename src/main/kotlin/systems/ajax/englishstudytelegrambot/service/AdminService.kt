package systems.ajax.englishstudytelegrambot.service

import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.User
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.UserRepository

interface AdminService {

    fun getAllUsers(): List<User>

    fun getAllLibraries(): List<Library>
}

@Service
class AdminServiceImpl(
    val userRepository: UserRepository,
    val libraryRepository: LibraryRepository
) : AdminService {

    override fun getAllUsers(): List<User> = userRepository.getAllUsers()

    override fun getAllLibraries(): List<Library> = libraryRepository.getAllLibraries()
}
