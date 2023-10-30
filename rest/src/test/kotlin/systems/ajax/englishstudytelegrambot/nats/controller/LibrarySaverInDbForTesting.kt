package systems.ajax.englishstudytelegrambot.nats.controller

import systems.ajax.englishstudytelegrambot.repository.LibraryRepository

object LibrarySaverInDbForTesting {

    fun LibraryRepository.saveLibraryForTesting(nameOfDeleteLibrary: String, telegramUserId: String) =
        saveNewLibrary(nameOfDeleteLibrary, telegramUserId)
            .block()!!
}
