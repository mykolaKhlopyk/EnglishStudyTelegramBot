import systems.ajax.infrastructure.mongo.repository.LibraryRepository

object LibrarySaverInMongoDbForTesting {

    fun LibraryRepository.saveLibraryForTesting(
        libraryName: String = "${System.nanoTime()} libraryName",
        telegramUserId: String = "${System.nanoTime()} telegramUserId"
    ) =
        saveNewLibrary(libraryName, telegramUserId)
            .block()!!
}
