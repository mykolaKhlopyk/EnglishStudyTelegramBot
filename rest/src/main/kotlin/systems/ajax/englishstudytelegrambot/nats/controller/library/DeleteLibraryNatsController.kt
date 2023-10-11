package systems.ajax.englishstudytelegrambot.nats.controller.library

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import library.DeleteLibrary.DeleteLibraryRequest
import library.DeleteLibrary.DeleteLibraryResponse
import library.DeleteLibrary.DeleteLibraryResponse.Success
import systems.ajax.NatsSubject.Library.DELETE_LIBRARY_SUBJECT
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toFailureResponse
import systems.ajax.englishstudytelegrambot.nats.mapper.toLibraryResponse
import systems.ajax.englishstudytelegrambot.service.LibraryService

@Component
class DeleteLibraryNatsController(private val libraryService: LibraryService) :
    NatsController<DeleteLibraryRequest, DeleteLibraryResponse> {

    override val subject: String = DELETE_LIBRARY_SUBJECT

    override val parser: Parser<DeleteLibraryRequest> = DeleteLibraryRequest.parser()

    override fun handle(request: DeleteLibraryRequest): DeleteLibraryResponse =
        runCatching {
            val deletedLibraryResponse = deleteLibraryInResponseFormat(request)
            DeleteLibraryResponse.newBuilder()
                .setSuccess(Success.newBuilder().setDeletedLibrary(deletedLibraryResponse))
                .build()
        }.getOrElse { exception ->
            DeleteLibraryResponse.newBuilder().setFailure(exception.toFailureResponse()).build()
        }

    private fun deleteLibraryInResponseFormat(request: DeleteLibraryRequest) =
        libraryService.deleteLibrary(request.libraryName, request.telegramUserId).toLibraryResponse()
}
