package systems.ajax.englishstudytelegrambot.exception

class WordAlreadyPresentInLibraryException(message: String) : RuntimeException(message)

class WordIsMissingException(message: String) : RuntimeException(message)
