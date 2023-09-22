package systems.ajax.englishstudytelegrambot.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import systems.ajax.englishstudytelegrambot.dto.TelegramUserIdDto
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.service.LibraryService

@RestController
@RequestMapping("/api/library")
class LibraryController(
    val libraryService: LibraryService
) {

    @PostMapping("/{nameOfNewLibrary}")
    fun createLibrary(
        @PathVariable("nameOfNewLibrary") nameOfNewLibrary: String,
        @RequestBody telegramUserIdDto: TelegramUserIdDto
    ): Library =
        libraryService.createNewLibrary(nameOfNewLibrary, telegramUserIdDto.telegramUserId)
}
