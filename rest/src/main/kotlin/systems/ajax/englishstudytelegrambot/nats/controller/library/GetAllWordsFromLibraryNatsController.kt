package systems.ajax.englishstudytelegrambot.nats.controller.library

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import systems.ajax.NatsSubject.Library.GET_ALL_WORDS_FROM_LIBRARY_SUBJECT
import systems.ajax.englishstudytelegrambot.entity.MongoWord
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toWordResponse
import systems.ajax.englishstudytelegrambot.service.LibraryService
import systems.ajax.entity.WordOuterClass
import systems.ajax.response_request.library.GetAllWordsFromLibrary.GetAllWordsFromLibraryRequest
import systems.ajax.response_request.library.GetAllWordsFromLibrary.GetAllWordsFromLibraryResponse

@Component
class GetAllWordsFromLibraryNatsController(private val libraryService: LibraryService) :
    NatsController<GetAllWordsFromLibraryRequest, GetAllWordsFromLibraryResponse> {

    override val subject: String = GET_ALL_WORDS_FROM_LIBRARY_SUBJECT

    override val parser: Parser<GetAllWordsFromLibraryRequest> = GetAllWordsFromLibraryRequest.parser()

    override fun handle(request: GetAllWordsFromLibraryRequest): GetAllWordsFromLibraryResponse =
        runCatching {
            val wordsFromLibrary = getAllWordsFromLibraryInResponseFormat(request)
            createSuccessResponse(wordsFromLibrary)
        }.getOrElse {
            createFailureResponse(it)
        }

    private fun getAllWordsFromLibraryInResponseFormat(request: GetAllWordsFromLibraryRequest) =
        libraryService.getAllWordsFromLibrary(request.libraryName, request.telegramUserId)
            .map(MongoWord::toWordResponse)

    private fun createSuccessResponse(wordsFromLibrary: List<WordOuterClass.Word>) =
        GetAllWordsFromLibraryResponse.newBuilder().apply {
            successBuilder.addAllWords(wordsFromLibrary)
        }.build()

    private fun createFailureResponse(exception: Throwable) =
        GetAllWordsFromLibraryResponse.newBuilder().apply {
            failureBuilder.setErrorMassage(exception.message).build()
        }.build()
}
