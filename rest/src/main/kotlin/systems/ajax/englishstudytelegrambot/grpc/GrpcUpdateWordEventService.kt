package systems.ajax.englishstudytelegrambot.grpc

import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.englishstudytelegrambot.nats.event.subscriber.EventSubscriber
import systems.ajax.event.word.UpdateWordEventOuterClass.UpdateWordEvent
import systems.ajax.service.GetUpdatesOfWordBySpellingRequest
import systems.ajax.service.ReactorUpdateWordEventServiceGrpc

@GrpcService
class GrpcUpdateWordEventService(val eventSubscriber: EventSubscriber<UpdateWordEvent>) :
    ReactorUpdateWordEventServiceGrpc.UpdateWordEventServiceImplBase() {

    override fun getUpdatesOfWordBySpelling(request: Mono<GetUpdatesOfWordBySpellingRequest>): Flux<UpdateWordEvent> =
        request.flatMapMany {
            eventSubscriber.subscribe(it.spelling)
        }
}
