package systems.ajax.englishstudytelegrambot.service

import reactor.core.publisher.Flux
import systems.ajax.englishstudytelegrambot.dto.entity.LibraryDtoResponse

interface UserService {

    fun getAllLibrariesOfUser(telegramUserId: String): Flux<LibraryDtoResponse>
}
