package systems.ajax

object NatsSubject {

    private const val SERVICE_NAME = "api.english"

    object Admin{
        private const val ADMIN_PATH = "$SERVICE_NAME.response_request.admin"

        const val GET_ALL_USERS_SUBJECT = "$ADMIN_PATH.get_all_users"
        const val GET_ALL_LIBRARIES_SUBJECT =  "$ADMIN_PATH.get_all_libraries"
        const val GET_ALL_WORDS_SUBJECT =  "$ADMIN_PATH.response_request.get_all_words"
    }

    object Library{
        private const val LIBRARY_PATH = "$SERVICE_NAME.response_request.library"

        const val GET_ALL_WORDS_FROM_LIBRARY_SUBJECT = "$LIBRARY_PATH.get_all_words_from_library"
        const val CREATE_NEW_LIBRARY_SUBJECT = "$LIBRARY_PATH.create_new_library"
        const val DELETE_LIBRARY_SUBJECT = "$LIBRARY_PATH.delete_all_words_from_library"
    }
}
