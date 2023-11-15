package systems.ajax.infrastructure.grpc

import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.application.ports.input.WordInPort
import systems.ajax.domain.model.Word
import systems.ajax.application.port.out.EventSubscriberOutPort
import systems.ajax.response_request.word.UpdateWordEvent
import systems.ajax.service.GetUpdatesOfWordBySpellingRequest
import systems.ajax.service.GetUpdatesOfWordBySpellingResponse
import systems.ajax.service.ReactorUpdateWordEventServiceGrpc

@GrpcService
class GrpcUpdateWordEventService(
    val eventSubscriberOutPort: EventSubscriberOutPort<UpdateWordEvent>,
    val wordService: WordInPort
) : ReactorUpdateWordEventServiceGrpc.UpdateWordEventServiceImplBase() {

    override fun getUpdatesOfWordBySpelling(request: Mono<GetUpdatesOfWordBySpellingRequest>): Flux<GetUpdatesOfWordBySpellingResponse> =
        request.flatMapMany {
            wordService.getAllWordsWithSpelling(it.spelling)
                .map(::createGetUpdatesOfWordBySpellingResponse)
                .concatWith(
                    eventSubscriberOutPort.subscribe(it.spelling)
                        .map(::createGetUpdatesOfWordBySpellingResponse))
        }

    private fun createGetUpdatesOfWordBySpellingResponse(word: Word): GetUpdatesOfWordBySpellingResponse =
        GetUpdatesOfWordBySpellingResponse.newBuilder()
            .setNewWordTranslate(word.translate)
            .setLibraryId(word.libraryId)
            .build()

    private fun createGetUpdatesOfWordBySpellingResponse(updateWordEvent: UpdateWordEvent): GetUpdatesOfWordBySpellingResponse =
        GetUpdatesOfWordBySpellingResponse.newBuilder()
            .setNewWordTranslate(updateWordEvent.newWordTranslate)
            .setLibraryId(updateWordEvent.libraryName)
            .build()
}
