package systems.ajax.englishstudytelegrambot.nats.controller.admin

import com.google.protobuf.Parser
import entity.LibraryOuterClass
import org.springframework.stereotype.Component
import admin.GetAllLibraries.GetAllLibrariesResponse
import admin.GetAllLibraries.GetAllLibrariesResponse.Success
import admin.GetAllLibraries.GetAllLibrariesRequest
import systems.ajax.NatsSubject.Admin.GET_ALL_LIBRARIES_SUBJECT
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toFailureResponse
import systems.ajax.englishstudytelegrambot.nats.mapper.toLibraryResponse
import systems.ajax.englishstudytelegrambot.service.AdminService

@Component
class GetAllLibrariesNatsController(
    private val adminService: AdminService
) : NatsController<GetAllLibrariesRequest, GetAllLibrariesResponse> {

    override val subject: String = GET_ALL_LIBRARIES_SUBJECT

    override val parser: Parser<GetAllLibrariesRequest> = GetAllLibrariesRequest.parser()

    override fun handle(request: GetAllLibrariesRequest): GetAllLibrariesResponse =
        runCatching {
            val librariesResponse = getAllLibrariesInResponseFormat()
            GetAllLibrariesResponse.newBuilder()
                .setSuccess(
                    Success.newBuilder().addAllLibraries(librariesResponse))
                .build()
        }.getOrElse { exception ->
            GetAllLibrariesResponse.newBuilder()
                .setFailure(exception.toFailureResponse())
                .build()
        }

    private fun getAllLibrariesInResponseFormat(): List<LibraryOuterClass.Library> =
        adminService.getAllLibraries().map(Library::toLibraryResponse)
}
