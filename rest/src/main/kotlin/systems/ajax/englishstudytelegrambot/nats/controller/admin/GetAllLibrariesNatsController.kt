package systems.ajax.englishstudytelegrambot.nats.controller.admin

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import systems.ajax.NatsSubject.Admin.GET_ALL_LIBRARIES_SUBJECT
import systems.ajax.englishstudytelegrambot.entity.MongoLibrary
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toLibraryResponse
import systems.ajax.englishstudytelegrambot.service.AdminService
import systems.ajax.entity.Library
import systems.ajax.response_request.admin.GetAllLibrariesRequest
import systems.ajax.response_request.admin.GetAllLibrariesResponse

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

    private fun getAllLibrariesInResponseFormat(): List<Library> =
        adminService.getAllLibraries().map(MongoLibrary::toLibraryResponse)

    private fun createSuccessResponse(librariesResponse: List<Library>) =
        GetAllLibrariesResponse.newBuilder().apply {
            successBuilder.addAllLibraries(librariesResponse)
        }.build()

    private fun createFailureResponse(exception: Throwable) =
        GetAllLibrariesResponse.newBuilder().apply {
            failureBuilder.setErrorMassage(exception.message)
        }.build()
}
