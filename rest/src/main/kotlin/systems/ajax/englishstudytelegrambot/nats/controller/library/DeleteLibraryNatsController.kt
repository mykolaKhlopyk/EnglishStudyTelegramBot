package systems.ajax.englishstudytelegrambot.nats.controller.library

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import response_request.DeleteLibrary.DeleteLibraryRequest
import response_request.DeleteLibrary.DeleteLibraryResponse
import systems.ajax.NatsSubject.DELETE_LIBRARY
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toLibraryResponse
import systems.ajax.englishstudytelegrambot.service.LibraryService

@Component
class DeleteLibraryNatsController (private val libraryService: LibraryService) :
    NatsController<DeleteLibraryRequest, DeleteLibraryResponse> {

    override val subject: String = DELETE_LIBRARY

    override val parser: Parser<DeleteLibraryRequest> = DeleteLibraryRequest.parser()

    override fun handle(request: DeleteLibraryRequest): DeleteLibraryResponse {
        val deletedLibrary: Library = libraryService.deleteLibrary(request.libraryName, request.telegramUserId)
        return DeleteLibraryResponse.newBuilder().setDeletedLibrary(deletedLibrary.toLibraryResponse()).build()
    }
}
