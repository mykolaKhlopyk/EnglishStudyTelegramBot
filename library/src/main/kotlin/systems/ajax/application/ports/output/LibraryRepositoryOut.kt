package systems.ajax.application.ports.output

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.domain.model.Library

interface LibraryRepositoryOut {

    fun saveNewLibrary(nameOfNewLibrary: String, telegramUserId: String): Mono<Library>

    fun deleteLibrary(libraryId: String): Mono<Library>

    fun getLibraryByLibraryNameAndTelegramUserId(libraryName: String, telegramUserId: String): Mono<Library>

    fun getLibraryIdByLibraryNameAndTelegramUserId(libraryName: String, telegramUserId: String): Mono<String>

    fun getLibraryById(id: String): Mono<Library>

    fun getAllLibrariesOfUser(telegramUserId: String): Flux<Library>
}
