package systems.ajax.englishstudytelegrambot.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import systems.ajax.englishstudytelegrambot.dto.entity.LibraryDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.toDtoResponse
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.repository.UserRepository

interface UserService {

    fun getAllLibrariesOfUser(telegramUserId: String): Flux<LibraryDtoResponse>
}

@Service
class UserServiceImpl(
    val userRepository: UserRepository,
) : UserService {

    override fun getAllLibrariesOfUser(telegramUserId: String): Flux<LibraryDtoResponse> =
        userRepository.getAllLibrariesOfUser(telegramUserId).map(Library::toDtoResponse)
}
