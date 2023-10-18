package systems.ajax.englishstudytelegrambot.nats.controller.admin

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import systems.ajax.NatsSubject.Admin.GET_ALL_WORDS_SUBJECT
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toWordResponse
import systems.ajax.englishstudytelegrambot.service.AdminService
import systems.ajax.entity.WordOuterClass
import systems.ajax.response_request.admin.GetAllWordsRequest
import systems.ajax.response_request.admin.GetAllWordsResponse
import systems.ajax.response_request.admin.GetAllWordsResponse.Success

@Component
class GetAllWordsNatsController(
    private val adminService: AdminService
) : NatsController<GetAllWordsRequest, GetAllWordsResponse> {

    override val subject: String = GET_ALL_WORDS_SUBJECT

    override val parser: Parser<GetAllWordsRequest> = GetAllWordsRequest.parser()

    override fun handle(request: GetAllWordsRequest): GetAllWordsResponse =
        runCatching {
            val wordsResponse = getAllWordsInResponseFormat()
            createSuccessResponse(wordsResponse)
        }.getOrElse {
            createFailureResponse(it)
        }

    private fun getAllWordsInResponseFormat(): List<WordOuterClass.Word> =
        adminService.getAllWords().map(Word::toWordResponse)

    private fun createSuccessResponse(wordsResponse: List<WordOuterClass.Word>) =
        GetAllWordsResponse.newBuilder().apply {
            successBuilder.addAllWords(wordsResponse)
        }.build()

    private fun createFailureResponse(exception: Throwable) =
        GetAllWordsResponse.newBuilder().apply {
            failureBuilder.setErrorMassage(exception.message).build()
        }.build()
}
