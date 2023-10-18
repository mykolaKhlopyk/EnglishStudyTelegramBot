package systems.ajax.englishstudytelegrambot.nats.controller.library

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import systems.ajax.NatsSubject.Library.DELETE_LIBRARY_SUBJECT
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toLibraryResponse
import systems.ajax.englishstudytelegrambot.service.LibraryService
import systems.ajax.entity.Library
import systems.ajax.entity.LibraryOuterClass
import systems.ajax.response_request.library.DeleteLibrary.DeleteLibraryRequest
import systems.ajax.response_request.library.DeleteLibrary.DeleteLibraryResponse
import systems.ajax.response_request.library.DeleteLibrary.DeleteLibraryResponse.Success

@Component
class DeleteLibraryNatsController(private val libraryService: LibraryService) :
    NatsController<DeleteLibraryRequest, DeleteLibraryResponse> {

    override val subject: String = DELETE_LIBRARY_SUBJECT

    override val parser: Parser<DeleteLibraryRequest> = DeleteLibraryRequest.parser()

    override fun handle(request: DeleteLibraryRequest): DeleteLibraryResponse =
        runCatching {
            val deletedLibraryResponse = deleteLibraryInResponseFormat(request)
            createSuccessResponse(deletedLibraryResponse)
        }.getOrElse {
            createFailureResponse(it)
        }

    private fun deleteLibraryInResponseFormat(request: DeleteLibraryRequest) =
        libraryService.deleteLibrary(request.libraryName, request.telegramUserId).toLibraryResponse()

    private fun createSuccessResponse(deletedLibraryResponse: Library) =
        DeleteLibraryResponse.newBuilder().apply {
            successBuilder.setDeletedLibrary(deletedLibraryResponse)
        }.build()

    private fun createFailureResponse(exception: Throwable) =
        DeleteLibraryResponse.newBuilder().apply {
            failureBuilder.setErrorMassage(exception.message).build()
        }.build()
}
