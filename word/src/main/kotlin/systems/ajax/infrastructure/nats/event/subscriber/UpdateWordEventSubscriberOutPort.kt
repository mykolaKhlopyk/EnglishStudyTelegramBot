package systems.ajax.infrastructure.nats.event.subscriber

import io.nats.client.Connection
import io.nats.client.Message
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import systems.ajax.infrastructure.nats.event.SubjectEventFactory.createUpdateWordEventSubject
import systems.ajax.application.port.out.EventSubscriberOutPort
import systems.ajax.response_request.word.UpdateWordEvent

@Component
class UpdateWordEventSubscriberOutPort(
    val natsConnection: Connection,
) : EventSubscriberOutPort<UpdateWordEvent> {

    override fun subscribe(eventKey: String): Flux<UpdateWordEvent> {
        val sinks: Sinks.Many<UpdateWordEvent> = Sinks.many().unicast().onBackpressureBuffer()
        makeNatsSubscriber(eventKey, sinks)
        return sinks.asFlux()
    }

    private fun makeNatsSubscriber(eventKey: String, sinks: Sinks.Many<UpdateWordEvent>) {
        natsConnection.createDispatcher { message: Message ->
            sinks.tryEmitNext(UpdateWordEvent.parseFrom(message.data))
        }.subscribe(createUpdateWordEventSubject(eventKey))
    }
}
