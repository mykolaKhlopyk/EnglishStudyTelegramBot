package systems.ajax.englishstudytelegrambot.service.impl

import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Qualifier
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
import systems.ajax.englishstudytelegrambot.repository.WordRepository
import systems.ajax.englishstudytelegrambot.service.LibraryService

@Service
class LibraryServiceImpl(
    val libraryRepository: LibraryRepository,
    @Qualifier("wordCashableRepositoryImpl") val wordRepository: WordRepository
) : LibraryService {

    override fun createNewLibrary(
        nameOfNewLibrary: String,
        telegramUserId: String
    ): Mono<Library> = libraryRepository
        .saveNewLibrary(nameOfNewLibrary, telegramUserId)
        .onErrorMap {
            LibraryWithTheSameNameForUserAlreadyExistException("library $nameOfNewLibrary is created")
        }

    override fun deleteLibrary(
        nameOfLibraryForDeleting: String,
        telegramUserId: String
    ): Mono<Library> = libraryRepository
        .getLibraryIdByLibraryNameAndTelegramUserId(
            nameOfLibraryForDeleting,
            telegramUserId
        )
        .switchIfEmpty(
            Mono.error(LibraryIsMissingException("libraries id was not found when try to delete library"))
        )
        .flatMap(libraryRepository::deleteLibrary)

    override fun getAllWordsFromLibrary(
        libraryName: String,
        telegramUserId: String
    ): Flux<Word> = libraryRepository
        .getLibraryIdByLibraryNameAndTelegramUserId(
            libraryName,
            telegramUserId
        )
        .switchIfEmpty(
            Mono.error(LibraryIsMissingException("library id was not found when try to get all words from it"))
        )
        .flatMapMany(wordRepository::getAllWordsFromLibrary)
        .doOnNext { GetAllWordsFromLibraryNatsController.log.info("get words {}", it) }

    override fun getLibraryById(id: ObjectId): Mono<Library> =
        libraryRepository.getLibraryById(id)
            .switchIfEmpty { Mono.error(LibraryIsMissingException("library with id $id is missing")) }
}
