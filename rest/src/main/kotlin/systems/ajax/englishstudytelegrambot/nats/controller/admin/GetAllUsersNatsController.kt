package systems.ajax.englishstudytelegrambot.nats.controller.admin

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import response_request.GetAllUsers.GetAllUsersRequest
import response_request.GetAllUsers.GetAllUsersResponse
import systems.ajax.NatsSubject.GET_ALL_USERS_SUBJECT
import systems.ajax.englishstudytelegrambot.entity.User
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.service.AdminService

@Component
class GetAllUsersNatsController(
    private val adminService: AdminService
) : NatsController<GetAllUsersRequest, GetAllUsersResponse> {

    override val subject: String = GET_ALL_USERS_SUBJECT

    override val parser: Parser<GetAllUsersRequest> = GetAllUsersRequest.parser()

    override fun handle(request: GetAllUsersRequest): GetAllUsersResponse {
        val userIds: List<String> = adminService.getAllUsers().map(User::telegramUserId)
        return GetAllUsersResponse.newBuilder().addAllTelegramUserIds(userIds).build()
    }
}
