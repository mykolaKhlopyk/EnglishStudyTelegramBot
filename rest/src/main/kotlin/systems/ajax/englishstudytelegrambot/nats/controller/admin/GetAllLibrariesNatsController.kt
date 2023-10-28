package systems.ajax.englishstudytelegrambot.nats.controller.admin

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.NatsSubject.Admin.GET_ALL_LIBRARIES_SUBJECT
import systems.ajax.englishstudytelegrambot.dto.entity.LibraryDtoResponse
import systems.ajax.entity.LibraryOuterClass.Library
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toLibraryResponse
import systems.ajax.englishstudytelegrambot.service.AdminService
import systems.ajax.response_request.admin.GetAllLibrariesRequest
import systems.ajax.response_request.admin.GetAllLibrariesResponse

@Component
class GetAllLibrariesNatsController(
    private val adminService: AdminService
) : NatsController<GetAllLibrariesRequest, GetAllLibrariesResponse> {

    override val subject: String = GET_ALL_LIBRARIES_SUBJECT

    override val parser: Parser<GetAllLibrariesRequest> = GetAllLibrariesRequest.parser()

    override fun handle(request: GetAllLibrariesRequest): Mono<GetAllLibrariesResponse> =
        getAllLibrariesInResponseFormat()
            .map { createSuccessResponse(it) }
            .onErrorResume { Mono.just(createFailureResponse(it)) }

    private fun getAllLibrariesInResponseFormat(): Mono<List<Library>> =
        adminService.getAllLibraries().map(LibraryDtoResponse::toLibraryResponse).collectList()

    private fun createSuccessResponse(librariesResponse: List<Library>): GetAllLibrariesResponse =
        GetAllLibrariesResponse.newBuilder().apply {
            successBuilder.addAllLibraries(librariesResponse)
        }.build()

    private fun createFailureResponse(exception: Throwable) =
        GetAllLibrariesResponse.newBuilder().apply {
            failureBuilder.setErrorMassage(exception.message)
        }.build()
}
