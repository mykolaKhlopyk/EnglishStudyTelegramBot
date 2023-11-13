package systems.ajax.application.ports.input

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.domain.model.Library

interface LibraryServiceIn {

    fun createNewLibrary(nameOfNewLibrary: String, telegramUserId: String): Mono<Library>

    fun deleteLibrary(nameOfLibraryForDeleting: String, telegramUserId: String): Mono<Library>

    fun getLibraryById(id: String): Mono<Library>

    fun getAllLibrariesOfUser(telegramUserId: String): Flux<Library>
}
