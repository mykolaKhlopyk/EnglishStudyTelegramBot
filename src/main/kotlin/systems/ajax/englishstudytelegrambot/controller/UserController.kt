package systems.ajax.englishstudytelegrambot.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import systems.ajax.englishstudytelegrambot.service.UserService

@RestController
@RequestMapping("api/users")
class UserController(val userService: UserService) {

    @GetMapping("/libraries")
    fun getAllUsersLibraries(@RequestBody telegramId: String) = userService.getAllLibrariesOfUser(telegramId)
}
