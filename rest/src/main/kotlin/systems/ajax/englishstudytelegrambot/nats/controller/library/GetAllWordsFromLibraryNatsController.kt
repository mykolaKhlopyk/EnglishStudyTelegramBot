package systems.ajax.englishstudytelegrambot.nats.controller.library

import com.google.protobuf.Parser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.NatsSubject.Library.GET_ALL_WORDS_FROM_LIBRARY_SUBJECT
import systems.ajax.englishstudytelegrambot.mapper.toWordResponse
import systems.ajax.entity.WordOuterClass.Word
import systems.ajax.englishstudytelegrambot.entity.Word as MongoWord
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController
import systems.ajax.englishstudytelegrambot.service.LibraryService
import systems.ajax.response_request.library.GetAllWordsFromLibrary.GetAllWordsFromLibraryRequest
import systems.ajax.response_request.library.GetAllWordsFromLibrary.GetAllWordsFromLibraryResponse

@Component
class GetAllWordsFromLibraryNatsController(private val libraryService: LibraryService) :
    NatsController<GetAllWordsFromLibraryRequest, GetAllWordsFromLibraryResponse> {

    override val subject: String = GET_ALL_WORDS_FROM_LIBRARY_SUBJECT

    override val parser: Parser<GetAllWordsFromLibraryRequest> = GetAllWordsFromLibraryRequest.parser()

    override fun handle(request: GetAllWordsFromLibraryRequest): Mono<GetAllWordsFromLibraryResponse> =
        getAllWordsFromLibraryInResponseFormat(request)
            .map { createSuccessResponse(it) }
            .onErrorResume { createFailureResponse(it).toMono() }

    private fun getAllWordsFromLibraryInResponseFormat(request: GetAllWordsFromLibraryRequest): Mono<List<Word>> =
        libraryService.getAllWordsFromLibrary(request.libraryName, request.telegramUserId)
            .doOnNext { log.info("get words {}", it) }
            .map(MongoWord::toWordResponse).collectList()

    private fun createSuccessResponse(wordsFromLibrary: List<Word>) =
        GetAllWordsFromLibraryResponse.newBuilder().apply {
            successBuilder.addAllWords(wordsFromLibrary)
        }.build()

    private fun createFailureResponse(exception: Throwable) =
        GetAllWordsFromLibraryResponse.newBuilder().apply {
            failureBuilder.setErrorMassage(exception.message).build()
        }.build()

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
