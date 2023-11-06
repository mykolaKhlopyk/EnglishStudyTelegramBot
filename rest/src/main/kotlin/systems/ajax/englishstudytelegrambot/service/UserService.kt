package systems.ajax.englishstudytelegrambot.service

import reactor.core.publisher.Flux
import systems.ajax.englishstudytelegrambot.entity.Library

interface UserService {

    fun getAllLibrariesOfUser(telegramUserId: String): Flux<Library>
}
