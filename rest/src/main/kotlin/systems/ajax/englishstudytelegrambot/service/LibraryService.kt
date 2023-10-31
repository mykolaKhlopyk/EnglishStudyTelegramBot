package systems.ajax.englishstudytelegrambot.service

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import systems.ajax.englishstudytelegrambot.dto.entity.LibraryDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.WordDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.toDtoResponse
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.exception.LibraryIsMissingException
import systems.ajax.englishstudytelegrambot.exception.LibraryWithTheSameNameForUserAlreadyExistException
import systems.ajax.englishstudytelegrambot.nats.controller.library.GetAllWordsFromLibraryNatsController
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository

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

@Service
class LibraryServiceImpl(
    val libraryRepository: LibraryRepository
) : LibraryService {

    override fun createNewLibrary(
        nameOfNewLibrary: String,
        telegramUserId: String
    ): Mono<LibraryDtoResponse> = libraryRepository
        .saveNewLibrary(nameOfNewLibrary, telegramUserId)
        .onErrorMap {
            LibraryWithTheSameNameForUserAlreadyExistException("library $nameOfNewLibrary is created")
        }
        .map(Library::toDtoResponse)

    override fun deleteLibrary(
        nameOfLibraryForDeleting: String,
        telegramUserId: String
    ): Mono<LibraryDtoResponse> = libraryRepository
        .getLibraryIdByLibraryNameAndTelegramUserId(
            nameOfLibraryForDeleting,
            telegramUserId
        )
        .switchIfEmpty(
            Mono.error(LibraryIsMissingException("libraries id was not found when try to delete library"))
        )
        .flatMap(libraryRepository::deleteLibrary)
        .map(Library::toDtoResponse)

    override fun getAllWordsFromLibrary(
        libraryName: String,
        telegramUserId: String
    ): Flux<WordDtoResponse> = libraryRepository
        .getLibraryIdByLibraryNameAndTelegramUserId(
            libraryName,
            telegramUserId
        )
        .switchIfEmpty(
            Mono.error(LibraryIsMissingException("library id was not found when try to get all words from it"))
        )
        .flatMapMany(libraryRepository::getAllWordsFromLibrary)
        .doOnNext { GetAllWordsFromLibraryNatsController.log.info("get words {}", it) }
        .map(Word::toDtoResponse)

    override fun getLibraryById(id: ObjectId): Mono<LibraryDtoResponse> =
        libraryRepository.getLibraryById(id).map(Library::toDtoResponse)
            .switchIfEmpty { Mono.error(LibraryIsMissingException("library with id $id is missing")) }
}
