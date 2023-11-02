package systems.ajax.englishstudytelegrambot.repository

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.englishstudytelegrambot.entity.Library

interface LibraryRepository {

    fun saveNewLibrary(nameOfNewLibrary: String, telegramUserId: String): Mono<Library>

    fun getAllLibraries(): Flux<Library>

    fun deleteLibrary(libraryId: ObjectId): Mono<Library>

    fun getLibraryByLibraryNameAndTelegramUserId(libraryName: String, telegramUserId: String): Mono<Library>

    fun getLibraryIdByLibraryNameAndTelegramUserId(libraryName: String, telegramUserId: String): Mono<ObjectId>

    fun getLibraryById(id: ObjectId): Mono<Library>
}
