package systems.ajax.englishstudytelegrambot.service

import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.dto.entity.LibraryDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.UserDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.WordDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.toDtoResponse
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.User
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.UserRepository
import systems.ajax.englishstudytelegrambot.repository.WordRepository

interface AdminService {

    fun getAllUsers(): List<UserDtoResponse>

    fun getAllLibraries(): List<LibraryDtoResponse>

    fun getAllWords(): List<WordDtoResponse>
}

@Service
class AdminServiceImpl(
    val userRepository: UserRepository,
    val libraryRepository: LibraryRepository,
    val wordRepository: WordRepository
) : AdminService {

    override fun getAllUsers(): List<UserDtoResponse> = userRepository.getAllUsers().map(User::toDtoResponse)

    override fun getAllLibraries(): List<LibraryDtoResponse> = libraryRepository.getAllLibraries().map(Library::toDtoResponse)

    override fun getAllWords(): List<WordDtoResponse> = wordRepository.getAllWords().map(Word::toDtoResponse)
}
