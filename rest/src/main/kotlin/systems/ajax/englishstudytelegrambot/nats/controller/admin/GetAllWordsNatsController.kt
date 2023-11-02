package systems.ajax.englishstudytelegrambot.nats.controller.admin

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.NatsSubject.Admin.GET_ALL_WORDS_SUBJECT
import systems.ajax.englishstudytelegrambot.dto.entity.WordDtoResponse
import systems.ajax.englishstudytelegrambot.entity.Word as MongoWord
import systems.ajax.entity.WordOuterClass.Word
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toWordResponse
import systems.ajax.englishstudytelegrambot.service.AdminService
import systems.ajax.response_request.admin.GetAllWordsRequest
import systems.ajax.response_request.admin.GetAllWordsResponse

@Component
class GetAllWordsNatsController(
    private val adminService: AdminService
) : NatsController<GetAllWordsRequest, GetAllWordsResponse> {

    override val subject: String = GET_ALL_WORDS_SUBJECT

    override val parser: Parser<GetAllWordsRequest> = GetAllWordsRequest.parser()

    override fun handle(request: GetAllWordsRequest): Mono<GetAllWordsResponse> =
        getAllWordsInResponseFormat()
            .map { createSuccessResponse(it) }
            .onErrorResume { createFailureResponse(it).toMono() }

    private fun getAllWordsInResponseFormat(): Mono<List<Word>> =
        adminService.getAllWords().map(MongoWord::toWordResponse).collectList()

    private fun createSuccessResponse(wordsResponse: List<Word>) =
        GetAllWordsResponse.newBuilder().apply {
            successBuilder.addAllWords(wordsResponse)
        }.build()

    private fun createFailureResponse(exception: Throwable) =
        GetAllWordsResponse.newBuilder().apply {
            failureBuilder.setErrorMassage(exception.message).build()
        }.build()
}
