package systems.ajax.englishstudytelegrambot.service

import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.entity.MongoLibrary
import systems.ajax.englishstudytelegrambot.entity.MongoUser
import systems.ajax.englishstudytelegrambot.entity.MongoWord
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.UserRepository
import systems.ajax.englishstudytelegrambot.repository.WordRepository

interface AdminService {

    fun getAllUsers(): List<MongoUser>

    fun getAllLibraries(): List<MongoLibrary>

    fun getAllWords(): List<MongoWord>
}

@Service
class AdminServiceImpl(
    val userRepository: UserRepository,
    val libraryRepository: LibraryRepository,
    val wordRepository: WordRepository
) : AdminService {

    override fun getAllUsers(): List<MongoUser> = userRepository.getAllUsers()

    override fun getAllLibraries(): List<MongoLibrary> = libraryRepository.getAllLibraries()

    override fun getAllWords(): List<MongoWord> = wordRepository.getAllWords()
}
