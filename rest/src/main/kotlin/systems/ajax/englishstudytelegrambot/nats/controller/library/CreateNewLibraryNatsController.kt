package systems.ajax.englishstudytelegrambot.nats.controller.library

import com.google.protobuf.Parser
import entity.LibraryOuterClass.Library
import library.CreateNewLibrary.CreateNewLibraryResponse.Success
import org.springframework.stereotype.Component
import library.CreateNewLibrary.CreateNewLibraryRequest
import library.CreateNewLibrary.CreateNewLibraryResponse
import systems.ajax.NatsSubject
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toFailureResponse
import systems.ajax.englishstudytelegrambot.nats.mapper.toLibraryResponse
import systems.ajax.englishstudytelegrambot.service.LibraryService

@Component
class CreateNewLibraryNatsController(private val libraryService: LibraryService) :
    NatsController<CreateNewLibraryRequest, CreateNewLibraryResponse> {

    override val subject: String = NatsSubject.CREATE_NEW_LIBRARY

    override val parser: Parser<CreateNewLibraryRequest> = CreateNewLibraryRequest.parser()

    override fun handle(request: CreateNewLibraryRequest): CreateNewLibraryResponse =
        runCatching {
            val createdLibrary = createdLibrary(request)
            CreateNewLibraryResponse.newBuilder()
                .setSuccess(Success.newBuilder().setCreatedLibrary(createdLibrary).build())
                .build()
        }.getOrElse { exception ->
            CreateNewLibraryResponse.newBuilder().setFailure(exception.toFailureResponse()).build()
        }

    private fun createdLibrary(request: CreateNewLibraryRequest): Library {
        val createdLibrary =
            libraryService.createNewLibrary(request.libraryName, request.telegramUserId).toLibraryResponse()
        return createdLibrary
    }
}
