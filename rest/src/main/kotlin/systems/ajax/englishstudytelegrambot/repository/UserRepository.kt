package systems.ajax.englishstudytelegrambot.repository

import reactor.core.publisher.Flux
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.User


interface UserRepository {

    fun getAllUsers(): Flux<User>

    fun getAllLibrariesOfUser(telegramUserId: String): Flux<Library>
}
