package systems.ajax.englishstudytelegrambot.service.impl

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
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
import systems.ajax.englishstudytelegrambot.service.AdminService

@Service
class AdminServiceImpl(
    val userRepository: UserRepository,
    val libraryRepository: LibraryRepository,
    @Qualifier("wordRepositoryImpl") val wordRepository: WordRepository
) : AdminService {

    override fun getAllUsers(): Flux<UserDtoResponse> =
        userRepository.getAllUsers().map(User::toDtoResponse)

    override fun getAllLibraries(): Flux<LibraryDtoResponse> =
        libraryRepository.getAllLibraries().map(Library::toDtoResponse)

    override fun getAllWords(): Flux<WordDtoResponse> =
        wordRepository.getAllWords().map(Word::toDtoResponse)
}
