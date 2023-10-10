package systems.ajax

object NatsSubject {

    private const val ADMIN_PATH = "api.admin."

    const val GET_ALL_USERS_SUBJECT = ADMIN_PATH + "getAllUsers"
    const val GET_ALL_LIBRARIES_SUBJECT = ADMIN_PATH + "getAllLibraries"
    const val GET_ALL_WORDS_SUBJECT = ADMIN_PATH + "getAllWords"

    private const val LIBRARY_PATH = "api.library."

    const val GET_ALL_WORDS_FROM_LIBRARY = LIBRARY_PATH + "getAllWordsFromLibrary"
    const val CREATE_NEW_LIBRARY = LIBRARY_PATH + "createNewLibrary"
    const val DELETE_LIBRARY = LIBRARY_PATH + "deleteLibrary"
}
