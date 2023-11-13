package systems.ajax.application.port.out

import reactor.core.publisher.Mono

interface WordsDeletingFromLibraryRepository {
    fun deleteAllWordsFromLibrary(libraryId: String): Mono<Unit>
}
