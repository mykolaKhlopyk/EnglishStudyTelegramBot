package systems.ajax.infrastructure.rest

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.application.ports.input.LibraryInPort
import systems.ajax.infrastructure.rest.dto.LibraryDtoResponse
import systems.ajax.infrastructure.rest.dto.toDtoResponse
import systems.ajax.domain.model.Library

@RestController
@RequestMapping("/api/library")
class LibraryController(
    private val libraryService: LibraryInPort
) {

    @GetMapping("/libraries")
    fun getAllUsersLibraries(
        @RequestHeader("telegramUserId") telegramUserId: String
    ): Flux<LibraryDtoResponse> = libraryService.getAllLibrariesOfUser(telegramUserId).map(Library::toDtoResponse)

    @PostMapping("/{nameOfNewLibrary}")
    fun createLibrary(
        @PathVariable("nameOfNewLibrary") nameOfNewLibrary: String,
        @RequestHeader("telegramUserId") telegramUserId: String
    ): Mono<LibraryDtoResponse> =
        libraryService.createNewLibrary(nameOfNewLibrary, telegramUserId).map(Library::toDtoResponse)

    @DeleteMapping("/{nameOfLibraryForDeleting}")
    fun deleteLibrary(
        @PathVariable("nameOfLibraryForDeleting") nameOfLibraryForDeleting: String,
        @RequestHeader("telegramUserId") telegramUserId: String
    ): Mono<LibraryDtoResponse> =
        libraryService.deleteLibrary(nameOfLibraryForDeleting, telegramUserId).map(Library::toDtoResponse)
}
