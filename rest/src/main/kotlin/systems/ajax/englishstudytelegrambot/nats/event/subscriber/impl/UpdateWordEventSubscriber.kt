package systems.ajax.englishstudytelegrambot.nats.event.subscriber.impl

import io.nats.client.Connection
import io.nats.client.Message
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import systems.ajax.englishstudytelegrambot.nats.event.SubjectEventFactory.createUpdateWordEventSubject
import systems.ajax.englishstudytelegrambot.nats.event.subscriber.EventSubscriber
import systems.ajax.event.word.UpdateWordEventOuterClass.UpdateWordEvent

@Component
class UpdateWordEventSubscriber(
    val natsConnection: Connection,
) : EventSubscriber<UpdateWordEvent> {

    val sinks: Sinks.Many<UpdateWordEvent> = Sinks.many().multicast().onBackpressureBuffer()

    override fun subscribe(eventKey: String): Flux<UpdateWordEvent> {
        makeNatsSubscriber(eventKey)
        return sinks.asFlux()
    }

    private fun makeNatsSubscriber(eventKey: String) {
        natsConnection.createDispatcher { message: Message ->
            sinks.tryEmitNext(UpdateWordEvent.parseFrom(message.data))
        }.subscribe(createUpdateWordEventSubject(eventKey))
    }
}
