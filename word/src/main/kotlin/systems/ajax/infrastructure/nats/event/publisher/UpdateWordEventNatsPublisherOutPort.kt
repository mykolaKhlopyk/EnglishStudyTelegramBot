package systems.ajax.infrastructure.nats.event.publisher

import org.springframework.stereotype.Component
import systems.ajax.application.port.out.EventPublisherOutPort
import systems.ajax.infrastructure.nats.NatsPublishFactory
import systems.ajax.infrastructure.nats.event.SubjectEventFactory.createUpdateWordEventSubject
import systems.ajax.response_request.word.UpdateWordEvent

@Component
class UpdateWordEventNatsPublisherOutPort(
    val natsPublishFactory: NatsPublishFactory,
) : EventPublisherOutPort<UpdateWordEvent> {

    override fun publish(message: UpdateWordEvent) {
        natsPublishFactory.publish(createUpdateWordEventSubject(message.spelling), message)
    }
}
