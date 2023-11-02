package systems.ajax.englishstudytelegrambot.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import systems.ajax.englishstudytelegrambot.dto.entity.LibraryDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.UserDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.WordDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.toDtoResponse
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.User
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.service.AdminService

@RestController
@RequestMapping("api/admin")
class AdminController(
    val adminService: AdminService
) {

    @GetMapping("/getAllLibraries")
    fun getAllLibraries(): Flux<LibraryDtoResponse> = adminService.getAllLibraries().map(Library::toDtoResponse)

    @GetMapping("/getAllUsers")
    fun getAllUsers(): Flux<UserDtoResponse> = adminService.getAllUsers().map(User::toDtoResponse)

    @GetMapping("/getAllWords")
    fun getAllWords(): Flux<WordDtoResponse> = adminService.getAllWords().map(Word::toDtoResponse)
}
