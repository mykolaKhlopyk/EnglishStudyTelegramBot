package systems.ajax.infrastructure.nats.event.publisher

import org.springframework.stereotype.Component
import systems.ajax.infrastructure.kafka.EventPublisher
import systems.ajax.infrastructure.nats.NatsPublishFactory
import systems.ajax.infrastructure.nats.event.SubjectEventFactory.createUpdateWordEventSubject
import systems.ajax.response_request.word.UpdateWordEvent

@Component
class UpdateWordEventNatsPublisher(
    val natsPublishFactory: NatsPublishFactory,
) : EventPublisher<UpdateWordEvent> {

    override fun publish(message: UpdateWordEvent) {
        natsPublishFactory.publish(createUpdateWordEventSubject(message.spelling), message)
    }
}
