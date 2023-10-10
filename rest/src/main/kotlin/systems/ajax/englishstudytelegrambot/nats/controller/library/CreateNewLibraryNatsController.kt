package systems.ajax.englishstudytelegrambot.nats.controller.library

import com.google.protobuf.Parser
import entity.LibraryOuterClass.Library
import org.springframework.stereotype.Component
import response_request.CreateNewLibrary.CreateNewLibraryRequest
import response_request.CreateNewLibrary.CreateNewLibraryResponse
import systems.ajax.NatsSubject
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toLibraryResponse
import systems.ajax.englishstudytelegrambot.service.LibraryService

@Component
class CreateNewLibraryNatsController(private val libraryService: LibraryService) :
    NatsController<CreateNewLibraryRequest, CreateNewLibraryResponse> {

    override val subject: String = NatsSubject.CREATE_NEW_LIBRARY

    override val parser: Parser<CreateNewLibraryRequest> = CreateNewLibraryRequest.parser()

    override fun handle(request: CreateNewLibraryRequest): CreateNewLibraryResponse {
        val createdLibrary: Library =
            libraryService.createNewLibrary(request.libraryName, request.telegramUserId).toLibraryResponse()
        return CreateNewLibraryResponse.newBuilder().setCreatedLibrary(createdLibrary).build()
    }
}
