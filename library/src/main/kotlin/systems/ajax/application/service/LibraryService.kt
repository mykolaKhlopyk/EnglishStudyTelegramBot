package systems.ajax.application.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import systems.ajax.application.ports.output.WordsDeletingFromLibraryRepositoryOutPort
import systems.ajax.application.ports.input.LibraryInPort
import systems.ajax.application.ports.output.LibraryRepositoryOutPort
import systems.ajax.domain.exception.LibraryNotFoundException
import systems.ajax.domain.exception.LibraryWithTheSameNameForUserAlreadyExistException
import systems.ajax.domain.model.Library

@Service
class LibraryService(
    private val libraryRepository: LibraryRepositoryOutPort,
    @Qualifier("wordRepository")private val wordsDeletingFromLibraryRepositoryOutPort: WordsDeletingFromLibraryRepositoryOutPort
) : LibraryInPort {

    override fun createNewLibrary(
        nameOfNewLibrary: String,
        telegramUserId: String,
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
        .flatMap { libraryId ->
            wordsDeletingFromLibraryRepositoryOutPort.deleteAllWordsFromLibrary(libraryId)
                .then(
                    libraryRepository.deleteLibrary(libraryId)
                )
        }
        .switchIfEmpty(
            Mono.error(LibraryNotFoundException("libraries id was not found when try to delete library"))
        )

    override fun getLibraryById(id: String): Mono<Library> =
        libraryRepository.getLibraryById(id)
            .switchIfEmpty { Mono.error(LibraryNotFoundException("library with id $id is missing")) }

    override fun getAllLibrariesOfUser(telegramUserId: String): Flux<Library> =
        libraryRepository.getAllLibrariesOfUser(telegramUserId)
}
