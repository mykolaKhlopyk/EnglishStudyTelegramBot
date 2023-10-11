package systems.ajax.englishstudytelegrambot.nats.controller.library

import com.google.protobuf.Parser
import entity.WordOuterClass
import org.springframework.stereotype.Component
import library.GetAllWordsFromLibrary.GetAllWordsFromLibraryRequest
import library.GetAllWordsFromLibrary.GetAllWordsFromLibraryResponse
import library.GetAllWordsFromLibrary.GetAllWordsFromLibraryResponse.Success
import systems.ajax.NatsSubject.Library.GET_ALL_WORDS_FROM_LIBRARY_SUBJECT
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.nats.mapper.toWordResponse
import systems.ajax.englishstudytelegrambot.service.LibraryService

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
            .map(Word::toWordResponse)

    private fun createSuccessResponse(wordsFromLibrary: List<WordOuterClass.Word>) =
        GetAllWordsFromLibraryResponse.newBuilder()
            .setSuccess(Success.newBuilder().addAllWords(wordsFromLibrary))
            .build()

    private fun createFailureResponse(exception: Throwable) =
        GetAllWordsFromLibraryResponse.newBuilder()
            .setFailure(
                GetAllWordsFromLibraryResponse.Failure.newBuilder().setErrorMassage(exception.message).build()
            ).build()
}
