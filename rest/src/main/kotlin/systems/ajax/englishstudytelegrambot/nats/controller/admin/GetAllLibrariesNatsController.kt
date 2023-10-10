package systems.ajax.englishstudytelegrambot.nats.controller.admin

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import response_request.GetAllLibraries.GetAllLibrariesResponse
import response_request.GetAllLibraries.GetAllLibrariesRequest
import systems.ajax.NatsSubject
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toLibraryResponse
import systems.ajax.englishstudytelegrambot.service.AdminService

@Component
class GetAllLibrariesNatsController(
    private val adminService: AdminService
) : NatsController<GetAllLibrariesRequest, GetAllLibrariesResponse> {

    override val subject: String = NatsSubject.GET_ALL_LIBRARIES_SUBJECT

    override val parser: Parser<GetAllLibrariesRequest> = GetAllLibrariesRequest.parser()

    override fun handle(request: GetAllLibrariesRequest): GetAllLibrariesResponse {
        val libraries = adminService.getAllLibraries()
            .map(Library::toLibraryResponse)
        return GetAllLibrariesResponse.newBuilder().addAllLibraries(libraries).build()
    }
}
