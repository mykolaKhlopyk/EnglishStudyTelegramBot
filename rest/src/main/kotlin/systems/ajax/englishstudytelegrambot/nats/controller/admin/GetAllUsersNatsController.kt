package systems.ajax.englishstudytelegrambot.nats.controller.admin

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import systems.ajax.NatsSubject.Admin.GET_ALL_USERS_SUBJECT
import systems.ajax.englishstudytelegrambot.entity.User
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.service.AdminService
import systems.ajax.response_request.admin.GetAllUsersRequest
import systems.ajax.response_request.admin.GetAllUsersResponse
import systems.ajax.response_request.admin.GetAllUsersResponse.Success

@Component
class GetAllUsersNatsController(
    private val adminService: AdminService
) : NatsController<GetAllUsersRequest, GetAllUsersResponse> {

    override val subject: String = GET_ALL_USERS_SUBJECT

    override val parser: Parser<GetAllUsersRequest> = GetAllUsersRequest.parser()

    override fun handle(request: GetAllUsersRequest): GetAllUsersResponse =
        runCatching {
            val userTelegramIds: List<String> = getTelegramUserIds()
            createSuccessResponse(userTelegramIds)
        }.getOrElse {
            createFailureResponse(it)
        }

    private fun getTelegramUserIds(): List<String> = adminService.getAllUsers().map(User::telegramUserId)

    private fun createSuccessResponse(userTelegramIds: List<String>) =
        GetAllUsersResponse.newBuilder()
            .setSuccess(Success.newBuilder().addAllTelegramUserIds(userTelegramIds))
            .build()

    private fun createFailureResponse(exception: Throwable) =
        GetAllUsersResponse.newBuilder().setFailure(
            GetAllUsersResponse.Failure.newBuilder().setErrorMassage(exception.message).build()
        ).build()
}
