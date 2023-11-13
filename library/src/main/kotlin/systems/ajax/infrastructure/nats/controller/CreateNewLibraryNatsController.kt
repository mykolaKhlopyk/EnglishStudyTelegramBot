package systems.ajax.infrastructure.nats.controller

import systems.ajax.infrastructure.nats.NatsController
import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.NatsSubject.Library.CREATE_NEW_LIBRARY_SUBJECT
import systems.ajax.application.service.LibraryService
import systems.ajax.domain.model.Library
import systems.ajax.infrastructure.nats.mapper.toProto
import systems.ajax.response_request.library.CreateNewLibraryRequest
import systems.ajax.response_request.library.CreateNewLibraryResponse
import systems.ajax.entity.Library as ProtoLibrary

@Component
class CreateNewLibraryNatsController(private val libraryService: LibraryService) :
    NatsController<CreateNewLibraryRequest, CreateNewLibraryResponse> {

    override val subject: String = CREATE_NEW_LIBRARY_SUBJECT

    override val parser: Parser<CreateNewLibraryRequest> = CreateNewLibraryRequest.parser()

    override fun handle(request: CreateNewLibraryRequest): Mono<CreateNewLibraryResponse> =
        createdLibrary(request)
            .map { createSuccessResponse(it) }
            .onErrorResume { createFailureResponse(it).toMono() }

    private fun createdLibrary(request: CreateNewLibraryRequest): Mono<ProtoLibrary> =
        libraryService.createNewLibrary(request.libraryName, request.telegramUserId)
            .map(Library::toProto)

    private fun createSuccessResponse(createdLibrary: ProtoLibrary) =
        CreateNewLibraryResponse.newBuilder().apply {
            successBuilder.setCreatedLibrary(createdLibrary)
        }.build()

    private fun createFailureResponse(exception: Throwable) =
        CreateNewLibraryResponse.newBuilder().apply {
            failureBuilder.setErrorMassage(exception.message).build()
        }.build()
}
