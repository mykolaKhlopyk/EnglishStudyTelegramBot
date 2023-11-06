package systems.ajax.englishstudytelegrambot.service

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.Word

interface LibraryService {

    fun createNewLibrary(
        nameOfNewLibrary: String,
        telegramUserId: String
    ): Mono<Library>

    fun deleteLibrary(
        nameOfLibraryForDeleting: String,
        telegramUserId: String
    ): Mono<Library>

    fun getAllWordsFromLibrary(
        libraryName: String,
        telegramUserId: String
    ): Flux<Word>

    fun getLibraryById(
        id: ObjectId
    ): Mono<Library>
}
