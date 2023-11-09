package systems.ajax.englishstudytelegrambot.nats.event.publisher

import org.springframework.stereotype.Component
import systems.ajax.englishstudytelegrambot.kafka.publisher.EventPublisher
import systems.ajax.englishstudytelegrambot.nats.NatsPublishFactory
import systems.ajax.englishstudytelegrambot.nats.event.SubjectEventFactory.createUpdateWordEventSubject
import systems.ajax.event.word.UpdateWordEventOuterClass.UpdateWordEvent

@Component
class UpdateWordEventPublisher(
    val natsPublishFactory: NatsPublishFactory,
) : EventPublisher<UpdateWordEvent> {

    override fun publish(message: UpdateWordEvent) {
        natsPublishFactory.publish(createUpdateWordEventSubject(message.wordSpelling), message.toByteArray())
    }
}
