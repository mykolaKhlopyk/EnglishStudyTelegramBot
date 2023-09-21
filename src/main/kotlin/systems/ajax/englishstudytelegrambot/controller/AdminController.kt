package systems.ajax.englishstudytelegrambot.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import systems.ajax.englishstudytelegrambot.service.AdminService

@RestController
@RequestMapping("api/admin")
class AdminController(
    val adminService: AdminService
) {
    @GetMapping("/getAll")
    fun getAll() = adminService.getAll()

    @GetMapping("/save")
    @PostMapping
    fun saveUser() = adminService.saveUser()
}
