package systems.ajax.englishstudytelegrambot.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import systems.ajax.englishstudytelegrambot.service.UserService

@RestController
@RequestMapping("api/users")
class UserController(val userService: UserService) {

    @GetMapping("/libraries")
    fun getAllUsersLibraries(
        @RequestHeader("telegramUserId") telegramUserId: String
    ) = userService.getAllLibrariesOfUser(telegramUserId)
}
