package systems.ajax.englishstudytelegrambot.exception

class LibraryIsMissingException(message: String) : RuntimeException(message)

class LibraryWithTheSameNameForUserAlreadyExistException(message: String) : RuntimeException(message)
