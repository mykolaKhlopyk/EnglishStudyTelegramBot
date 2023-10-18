package systems.ajax.englishstudytelegrambot.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import systems.ajax.englishstudytelegrambot.entity.MongoLibrary
import systems.ajax.englishstudytelegrambot.entity.MongoUser
import systems.ajax.englishstudytelegrambot.entity.MongoWord
import systems.ajax.englishstudytelegrambot.service.AdminService

@RestController
@RequestMapping("api/admin")
class AdminController(
    val adminService: AdminService
) {

    @GetMapping("/getAllLibraries")
    fun getAllLibraries(): List<MongoLibrary> = adminService.getAllLibraries()

    @GetMapping("/getAllUsers")
    fun getAllUsers(): List<MongoUser> = adminService.getAllUsers()

    @GetMapping("/getAllWords")
    fun getAllWords(): List<MongoWord> = adminService.getAllWords()
}
