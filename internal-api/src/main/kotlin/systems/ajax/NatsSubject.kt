package systems.ajax

object NatsSubject {

    private const val SERVICE_NAME = "api.english"

    object Library{
        private const val LIBRARY_PATH = "$SERVICE_NAME.response_request.library"

        const val GET_ALL_WORDS_FROM_LIBRARY_SUBJECT = "$LIBRARY_PATH.get_all_words_from_library"
        const val CREATE_NEW_LIBRARY_SUBJECT = "$LIBRARY_PATH.create_new_library"
        const val DELETE_LIBRARY_SUBJECT = "$LIBRARY_PATH.delete_all_words_from_library"
    }
}
