package systems.ajax.englishstudytelegrambot.nats.controller.admin

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import admin.GetAllUsersRequest
import admin.GetAllUsersResponse
import admin.GetAllUsersResponse.Success
import systems.ajax.NatsSubject.Admin.GET_ALL_USERS_SUBJECT
import systems.ajax.englishstudytelegrambot.entity.User
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toFailureResponse
import systems.ajax.englishstudytelegrambot.service.AdminService

@Component
class GetAllUsersNatsController(
    private val adminService: AdminService
) : NatsController<GetAllUsersRequest, GetAllUsersResponse> {

    override val subject: String = GET_ALL_USERS_SUBJECT

    override val parser: Parser<GetAllUsersRequest> = GetAllUsersRequest.parser()

    override fun handle(request: GetAllUsersRequest): GetAllUsersResponse =
        runCatching {
            val userTelegramIds: List<String> = getTelegramUserIds()
            GetAllUsersResponse.newBuilder()
                .setSuccess(Success.newBuilder().addAllTelegramUserIds(userTelegramIds))
                .build()
        }.getOrElse { exception ->
            GetAllUsersResponse.newBuilder()
                .setFailure(exception.toFailureResponse())
                .build()
        }

    private fun getTelegramUserIds(): List<String> = adminService.getAllUsers().map(User::telegramUserId)
}
