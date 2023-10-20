package systems.ajax.englishstudytelegrambot.controller

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.DeleteMapping
import systems.ajax.englishstudytelegrambot.dto.entity.LibraryDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.WordDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.toDtoResponse
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.service.LibraryService

@RestController
@RequestMapping("/api/library")
class LibraryController(
    val libraryService: LibraryService
) {

    @GetMapping("/{libraryName}")
    fun getAllWordsFromLibrary(
        @PathVariable("libraryName") libraryName: String,
        @RequestHeader("telegramUserId") telegramUserId: String
    ): List<WordDtoResponse> =
        libraryService.getAllWordsFromLibrary(libraryName, telegramUserId)

    @PostMapping("/{nameOfNewLibrary}")
    fun createLibrary(
        @PathVariable("nameOfNewLibrary") nameOfNewLibrary: String,
        @RequestHeader("telegramUserId") telegramUserId: String
    ): LibraryDtoResponse =
        libraryService.createNewLibrary(nameOfNewLibrary, telegramUserId)

    @DeleteMapping("/{nameOfLibraryForDeleting}")
    fun deleteLibrary(
        @PathVariable("nameOfLibraryForDeleting") nameOfLibraryForDeleting: String,
        @RequestHeader("telegramUserId") telegramUserId: String
    ): LibraryDtoResponse =
        libraryService.deleteLibrary(nameOfLibraryForDeleting, telegramUserId)
}
