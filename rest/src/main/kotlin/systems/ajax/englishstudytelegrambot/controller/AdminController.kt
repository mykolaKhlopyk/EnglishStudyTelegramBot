package systems.ajax.englishstudytelegrambot.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
    fun getAllLibraries(): List<Library> = adminService.getAllLibraries()

    @GetMapping("/getAllUsers")
    fun getAllUsers(): List<User> = adminService.getAllUsers()

    @GetMapping("/getAllWords")
    fun getAllWords(): List<Word> = adminService.getAllWords()
}
