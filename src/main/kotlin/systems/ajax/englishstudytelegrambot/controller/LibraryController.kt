package systems.ajax.englishstudytelegrambot.controller

import org.springframework.web.bind.annotation.*
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
        @RequestHeader("telegramUserId") telegramUserId: String
    ): Library =
        libraryService.createNewLibrary(nameOfNewLibrary, telegramUserId)

    @DeleteMapping("/{nameOfLibraryForDeleting}")
    fun deleteLibrary(
        @PathVariable("nameOfLibraryForDeleting") nameOfLibraryForDeleting: String,
        @RequestHeader("telegramUserId") telegramUserId: String
    ): Library =
        libraryService.deleteLibrary(nameOfLibraryForDeleting, telegramUserId)

}
