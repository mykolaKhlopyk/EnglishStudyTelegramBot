package systems.ajax.englishstudytelegrambot.nats.controller.library

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import systems.ajax.NatsSubject.Library.CREATE_NEW_LIBRARY_SUBJECT
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toLibraryResponse
import systems.ajax.englishstudytelegrambot.service.LibraryService
import systems.ajax.entity.LibraryOuterClass
import systems.ajax.response_request.library.CreateNewLibrary.CreateNewLibraryRequest
import systems.ajax.response_request.library.CreateNewLibrary.CreateNewLibraryResponse
import systems.ajax.response_request.library.CreateNewLibrary.CreateNewLibraryResponse.Success

@Component
class CreateNewLibraryNatsController(private val libraryService: LibraryService) :
    NatsController<CreateNewLibraryRequest, CreateNewLibraryResponse> {

    override val subject: String = CREATE_NEW_LIBRARY_SUBJECT

    override val parser: Parser<CreateNewLibraryRequest> = CreateNewLibraryRequest.parser()

    override fun handle(request: CreateNewLibraryRequest): CreateNewLibraryResponse =
        runCatching {
            val createdLibrary = createdLibrary(request)
            createSuccessResponse(createdLibrary)
        }.getOrElse {
            createFailureResponse(it)
        }

    private fun createdLibrary(request: CreateNewLibraryRequest): LibraryOuterClass.Library {
        val createdLibrary =
            libraryService.createNewLibrary(request.libraryName, request.telegramUserId).toLibraryResponse()
        return createdLibrary
    }

    private fun createSuccessResponse(createdLibrary: LibraryOuterClass.Library) =
        CreateNewLibraryResponse.newBuilder()
            .setSuccess(Success.newBuilder().setCreatedLibrary(createdLibrary).build())
            .build()

    private fun createFailureResponse(exception: Throwable) =
        CreateNewLibraryResponse.newBuilder().setFailure(
            CreateNewLibraryResponse.Failure.newBuilder().setErrorMassage(exception.message).build()
        ).build()
}
