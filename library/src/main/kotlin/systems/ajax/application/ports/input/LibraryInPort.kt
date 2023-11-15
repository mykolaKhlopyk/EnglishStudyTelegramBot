package systems.ajax.application.ports.input

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.domain.model.Library

interface LibraryInPort {

    fun createNewLibrary(nameOfNewLibrary: String, telegramUserId: String): Mono<Library>

    fun deleteLibrary(nameOfLibraryForDeleting: String, telegramUserId: String): Mono<Library>

    fun getLibraryById(id: String): Mono<Library>

    fun getAllLibrariesOfUser(telegramUserId: String): Flux<Library>
}
