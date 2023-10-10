package systems.ajax.englishstudytelegrambot.nats.controller.admin

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import response_request.GetAllWords.GetAllWordsResponse
import response_request.GetAllWords.GetAllWordsRequest
import systems.ajax.NatsSubject.GET_ALL_WORDS_SUBJECT
import systems.ajax.englishstudytelegrambot.controller.AdminController
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toWordResponse

@Component
class GetAllWordsNatsController(
    private val adminController: AdminController
) : NatsController<GetAllWordsRequest, GetAllWordsResponse> {

    override val subject: String = GET_ALL_WORDS_SUBJECT

    override val parser: Parser<GetAllWordsRequest> = GetAllWordsRequest.parser()

    override fun handle(request: GetAllWordsRequest): GetAllWordsResponse {
        val words = adminController.getAllWords().map(Word::toWordResponse)
        return GetAllWordsResponse.newBuilder().addAllWords(words).build()
    }
}
