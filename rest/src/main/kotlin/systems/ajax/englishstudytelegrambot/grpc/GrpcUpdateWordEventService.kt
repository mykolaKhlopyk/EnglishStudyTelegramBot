package systems.ajax.englishstudytelegrambot.grpc

import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.nats.event.subscriber.EventSubscriber
import systems.ajax.englishstudytelegrambot.service.WordService
import systems.ajax.event.word.UpdateWordEventOuterClass.UpdateWordEvent
import systems.ajax.service.GetUpdatesOfWordBySpellingRequest
import systems.ajax.service.GetUpdatesOfWordBySpellingResponse
import systems.ajax.service.ReactorUpdateWordEventServiceGrpc

@GrpcService
class GrpcUpdateWordEventService(
    val eventSubscriber: EventSubscriber<UpdateWordEvent>,
    val wordService: WordService,
) : ReactorUpdateWordEventServiceGrpc.UpdateWordEventServiceImplBase() {

    override fun getUpdatesOfWordBySpelling(request: Mono<GetUpdatesOfWordBySpellingRequest>): Flux<GetUpdatesOfWordBySpellingResponse> =
        request.flatMapMany {
            wordService.getAllWordsWithSpelling(it.spelling)
                .map(::createGetUpdatesOfWordBySpellingResponse)
                .concatWith(
                    eventSubscriber.subscribe(it.spelling)
                        .map(::createGetUpdatesOfWordBySpellingResponse))
        }

    private fun createGetUpdatesOfWordBySpellingResponse(word: Word): GetUpdatesOfWordBySpellingResponse =
        GetUpdatesOfWordBySpellingResponse.newBuilder()
            .setNewWordTranslate(word.translate)
            .setLibraryId(word.libraryId.toHexString())
            .build()

    private fun createGetUpdatesOfWordBySpellingResponse(updateWordEvent: UpdateWordEvent): GetUpdatesOfWordBySpellingResponse =
        GetUpdatesOfWordBySpellingResponse.newBuilder()
            .setNewWordTranslate(updateWordEvent.newWordTranslate)
            .setLibraryId(updateWordEvent.libraryName)
            .build()
}
