package systems.ajax.application.ports.output

import reactor.core.publisher.Mono

interface  WordsDeletingFromLibraryRepositoryOutPort {
    fun deleteAllWordsFromLibrary(libraryId: String): Mono<Unit>
}
