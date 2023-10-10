package systems.ajax.englishstudytelegrambot.nats.controller.library

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import response_request.GetAllWordsFromLibrary.GetAllWordsFromLibraryRequest
import response_request.GetAllWordsFromLibrary.GetAllWordsFromLibraryResponse
import systems.ajax.NatsSubject
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toWordResponse
import systems.ajax.englishstudytelegrambot.service.LibraryService

@Component
class GetAllWordsFromLibraryNatsController(private val libraryService: LibraryService) :
    NatsController<GetAllWordsFromLibraryRequest, GetAllWordsFromLibraryResponse> {

    override val subject: String = NatsSubject.GET_ALL_WORDS_FROM_LIBRARY

    override val parser: Parser<GetAllWordsFromLibraryRequest> = GetAllWordsFromLibraryRequest.parser()

    override fun handle(request: GetAllWordsFromLibraryRequest): GetAllWordsFromLibraryResponse {
        val words = libraryService.getAllWordsFromLibrary(request.libraryName, request.telegramUserId).map(Word::toWordResponse)
        return GetAllWordsFromLibraryResponse.newBuilder().addAllWords(words).build()
    }
}
