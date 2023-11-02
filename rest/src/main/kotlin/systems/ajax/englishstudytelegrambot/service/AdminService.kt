package systems.ajax.englishstudytelegrambot.service

import reactor.core.publisher.Flux
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.User
import systems.ajax.englishstudytelegrambot.entity.Word

interface AdminService {

    fun getAllUsers(): Flux<User>

    fun getAllLibraries(): Flux<Library>

    fun getAllWords(): Flux<Word>
}
