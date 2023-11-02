package systems.ajax.englishstudytelegrambot.service

import reactor.core.publisher.Flux
import systems.ajax.englishstudytelegrambot.dto.entity.LibraryDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.UserDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.WordDtoResponse

interface AdminService {

    fun getAllUsers(): Flux<UserDtoResponse>

    fun getAllLibraries(): Flux<LibraryDtoResponse>

    fun getAllWords(): Flux<WordDtoResponse>
}
