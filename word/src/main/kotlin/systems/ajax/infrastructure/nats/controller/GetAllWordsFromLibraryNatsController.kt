package systems.ajax.infrastructure.nats.controller

import systems.ajax.infrastructure.nats.NatsController
import com.google.protobuf.Parser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.NatsSubject.Library.GET_ALL_WORDS_FROM_LIBRARY_SUBJECT
import systems.ajax.application.ports.input.WordServiceIn
import systems.ajax.domain.model.Word
import systems.ajax.infrastructure.nats.mapper.toProto
import systems.ajax.entity.Word as ProtoWord
import systems.ajax.response_request.library.GetAllWordsFromLibraryRequest
import systems.ajax.response_request.library.GetAllWordsFromLibraryResponse

@Component
class GetAllWordsFromLibraryNatsController(private val wordService: WordServiceIn) :
    NatsController<GetAllWordsFromLibraryRequest, GetAllWordsFromLibraryResponse> {

    override val subject: String = GET_ALL_WORDS_FROM_LIBRARY_SUBJECT

    override val parser: Parser<GetAllWordsFromLibraryRequest> = GetAllWordsFromLibraryRequest.parser()

    override fun handle(request: GetAllWordsFromLibraryRequest): Mono<GetAllWordsFromLibraryResponse> =
        getAllWordsFromLibraryInResponseFormat(request)
            .map { createSuccessResponse(it) }
            .onErrorResume { createFailureResponse(it).toMono() }

    private fun getAllWordsFromLibraryInResponseFormat(request: GetAllWordsFromLibraryRequest): Mono<List<ProtoWord>> =
        wordService.getAllWordsFromLibrary(request.libraryName, request.telegramUserId)
            .doOnNext { log.info("get words {}", it) }
            .map(Word::toProto).collectList()

    private fun createSuccessResponse(wordsFromLibrary: List<ProtoWord>) =
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
