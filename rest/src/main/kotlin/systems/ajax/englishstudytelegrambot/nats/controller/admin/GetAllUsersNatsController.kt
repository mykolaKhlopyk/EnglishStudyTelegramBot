package systems.ajax.englishstudytelegrambot.nats.controller.admin

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.NatsSubject.Admin.GET_ALL_USERS_SUBJECT
import systems.ajax.englishstudytelegrambot.dto.entity.UserDtoResponse
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.service.AdminService
import systems.ajax.response_request.admin.GetAllUsersRequest
import systems.ajax.response_request.admin.GetAllUsersResponse

@Component
class GetAllUsersNatsController(
    private val adminService: AdminService
) : NatsController<GetAllUsersRequest, GetAllUsersResponse> {

    override val subject: String = GET_ALL_USERS_SUBJECT

    override val parser: Parser<GetAllUsersRequest> = GetAllUsersRequest.parser()

    override fun handle(request: GetAllUsersRequest): Mono<GetAllUsersResponse> =
        getTelegramUserIds()
            .map { createSuccessResponse(it) }
            .onErrorResume { createFailureResponse(it).toMono() }

    private fun getTelegramUserIds(): Mono<List<String>> =
        adminService.getAllUsers().map(UserDtoResponse::telegramUserId).collectList()

    private fun createSuccessResponse(telegramUserIds: List<String>) =
        GetAllUsersResponse.newBuilder().apply {
            successBuilder.addAllTelegramUserIds(telegramUserIds)
        }.build()

    private fun createFailureResponse(exception: Throwable) =
        GetAllUsersResponse.newBuilder().apply {
            failureBuilder.setErrorMassage(exception.message).build()
        }.build()
}
