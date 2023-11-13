package systems.ajax.infrastructure.nats.controller

import systems.ajax.infrastructure.nats.NatsController
import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.NatsSubject.Library.DELETE_LIBRARY_SUBJECT
import systems.ajax.application.ports.input.LibraryServiceIn
import systems.ajax.domain.model.Library
import systems.ajax.infrastructure.nats.mapper.toProto
import systems.ajax.response_request.library.DeleteLibraryRequest
import systems.ajax.response_request.library.DeleteLibraryResponse
import systems.ajax.entity.Library as ProtoLibrary

@Component
class DeleteLibraryNatsController(private val libraryService: LibraryServiceIn) :
    NatsController<DeleteLibraryRequest, DeleteLibraryResponse> {

    override val subject: String = DELETE_LIBRARY_SUBJECT

    override val parser: Parser<DeleteLibraryRequest> = DeleteLibraryRequest.parser()

    override fun handle(request: DeleteLibraryRequest): Mono<DeleteLibraryResponse> =
        deleteLibraryInResponseFormat(request)
            .map { createSuccessResponse(it) }
            .onErrorResume { createFailureResponse(it).toMono() }

    private fun deleteLibraryInResponseFormat(request: DeleteLibraryRequest) =
        libraryService.deleteLibrary(request.libraryName, request.telegramUserId)
            .map(Library::toProto)

    private fun createSuccessResponse(deletedLibraryResponse: ProtoLibrary) =
        DeleteLibraryResponse.newBuilder().apply {
            successBuilder.setDeletedLibrary(deletedLibraryResponse)
        }.build()

    private fun createFailureResponse(exception: Throwable) =
        DeleteLibraryResponse.newBuilder().apply {
            failureBuilder.setErrorMassage(exception.message).build()
        }.build()
}
