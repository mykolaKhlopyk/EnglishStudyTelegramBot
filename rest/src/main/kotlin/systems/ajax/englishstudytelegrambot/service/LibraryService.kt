package systems.ajax.englishstudytelegrambot.service

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.englishstudytelegrambot.dto.entity.LibraryDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.WordDtoResponse

interface LibraryService {

    fun createNewLibrary(
        nameOfNewLibrary: String,
        telegramUserId: String
    ): Mono<LibraryDtoResponse>

    fun deleteLibrary(
        nameOfLibraryForDeleting: String,
        telegramUserId: String
    ): Mono<LibraryDtoResponse>

    fun getAllWordsFromLibrary(
        libraryName: String,
        telegramUserId: String
    ): Flux<WordDtoResponse>

    fun getLibraryById(
        id: ObjectId
    ): Mono<LibraryDtoResponse>
}
