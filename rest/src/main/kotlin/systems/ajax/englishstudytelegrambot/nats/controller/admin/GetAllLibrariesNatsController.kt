package systems.ajax.englishstudytelegrambot.nats.controller.admin

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import systems.ajax.NatsSubject.Admin.GET_ALL_LIBRARIES_SUBJECT
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toLibraryResponse
import systems.ajax.englishstudytelegrambot.service.AdminService
import systems.ajax.entity.LibraryOuterClass
import systems.ajax.response_request.admin.GetAllLibrariesRequest
import systems.ajax.response_request.admin.GetAllLibrariesResponse
import systems.ajax.response_request.admin.GetAllLibrariesResponse.Success

@Component
class GetAllLibrariesNatsController(
    private val adminService: AdminService
) : NatsController<GetAllLibrariesRequest, GetAllLibrariesResponse> {

    override val subject: String = GET_ALL_LIBRARIES_SUBJECT

    override val parser: Parser<GetAllLibrariesRequest> = GetAllLibrariesRequest.parser()

    override fun handle(request: GetAllLibrariesRequest): GetAllLibrariesResponse =
        runCatching {
            val librariesResponse = getAllLibrariesInResponseFormat()
            createSuccessResponse(librariesResponse)
        }.getOrElse {
            createFailureResponse(it)
        }

    private fun getAllLibrariesInResponseFormat(): List<LibraryOuterClass.Library> =
        adminService.getAllLibraries().map(Library::toLibraryResponse)

    private fun createSuccessResponse(librariesResponse: List<LibraryOuterClass.Library>) =
        GetAllLibrariesResponse.newBuilder().apply {
            successBuilder.addAllLibraries(librariesResponse)
        }.build()

    private fun createFailureResponse(exception: Throwable) =
        GetAllLibrariesResponse.newBuilder().apply {
            failureBuilder.setErrorMassage(exception.message)
        }.build()
}
