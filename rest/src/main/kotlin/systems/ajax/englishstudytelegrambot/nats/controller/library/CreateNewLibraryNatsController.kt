package systems.ajax.englishstudytelegrambot.nats.controller.library

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.NatsSubject.Library.CREATE_NEW_LIBRARY_SUBJECT
import systems.ajax.englishstudytelegrambot.dto.entity.LibraryDtoResponse
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toLibraryResponse
import systems.ajax.englishstudytelegrambot.service.LibraryService
import systems.ajax.entity.LibraryOuterClass.Library
import systems.ajax.response_request.library.CreateNewLibrary.CreateNewLibraryRequest
import systems.ajax.response_request.library.CreateNewLibrary.CreateNewLibraryResponse

@Component
class CreateNewLibraryNatsController(private val libraryService: LibraryService) :
    NatsController<CreateNewLibraryRequest, CreateNewLibraryResponse> {

    override val subject: String = CREATE_NEW_LIBRARY_SUBJECT

    override val parser: Parser<CreateNewLibraryRequest> = CreateNewLibraryRequest.parser()

    override fun handle(request: CreateNewLibraryRequest): Mono<CreateNewLibraryResponse> =
        createdLibrary(request)
            .map { createSuccessResponse(it) }
            .onErrorResume { createFailureResponse(it).toMono() }

    private fun createdLibrary(request: CreateNewLibraryRequest): Mono<Library> =
        libraryService.createNewLibrary(request.libraryName, request.telegramUserId)
            .map(LibraryDtoResponse::toLibraryResponse)


    private fun createSuccessResponse(createdLibrary: Library) =
        CreateNewLibraryResponse.newBuilder().apply {
            successBuilder.setCreatedLibrary(createdLibrary)
        }.build()

    private fun createFailureResponse(exception: Throwable) =
        CreateNewLibraryResponse.newBuilder().apply {
            failureBuilder.setErrorMassage(exception.message).build()
        }.build()
}
