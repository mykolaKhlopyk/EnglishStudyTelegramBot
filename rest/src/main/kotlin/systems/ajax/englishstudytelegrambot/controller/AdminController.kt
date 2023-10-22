package systems.ajax.englishstudytelegrambot.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import systems.ajax.englishstudytelegrambot.dto.entity.LibraryDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.UserDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.WordDtoResponse
import systems.ajax.englishstudytelegrambot.service.AdminService

@RestController
@RequestMapping("api/admin")
class AdminController(
    val adminService: AdminService
) {

    @GetMapping("/getAllLibraries")
    fun getAllLibraries(): Flux<LibraryDtoResponse> = adminService.getAllLibraries()

    @GetMapping("/getAllUsers")
    fun getAllUsers(): Flux<UserDtoResponse> = adminService.getAllUsers()

    @GetMapping("/getAllWords")
    fun getAllWords(): Flux<WordDtoResponse> = adminService.getAllWords()
}
