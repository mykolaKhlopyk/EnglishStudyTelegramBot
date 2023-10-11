package systems.ajax.englishstudytelegrambot.nats.controller.admin

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import admin.GetAllWords.GetAllWordsResponse
import admin.GetAllWords.GetAllWordsResponse.Success
import admin.GetAllWords.GetAllWordsRequest
import entity.WordOuterClass
import systems.ajax.NatsSubject.GET_ALL_WORDS_SUBJECT
import systems.ajax.englishstudytelegrambot.controller.AdminController
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toFailureResponse
import systems.ajax.englishstudytelegrambot.nats.mapper.toWordResponse
import systems.ajax.englishstudytelegrambot.service.AdminService

@Component
class GetAllWordsNatsController(
    private val adminService: AdminService
) : NatsController<GetAllWordsRequest, GetAllWordsResponse> {

    override val subject: String = GET_ALL_WORDS_SUBJECT

    override val parser: Parser<GetAllWordsRequest> = GetAllWordsRequest.parser()

    override fun handle(request: GetAllWordsRequest): GetAllWordsResponse =
        runCatching {
            val wordsResponse = getAllWordsInResponseFormat()
            GetAllWordsResponse.newBuilder()
                .setSuccess(Success.newBuilder().addAllWords(wordsResponse).build())
                .build()
        }.getOrElse { exception ->
            GetAllWordsResponse.newBuilder().setFailure(
                exception.toFailureResponse()
            ).build()
        }

    private fun getAllWordsInResponseFormat(): List<WordOuterClass.Word> =
        adminService.getAllWords().map(Word::toWordResponse)
}
