package systems.ajax.englishstudytelegrambot.nats.publisher

import org.springframework.stereotype.Component
import systems.ajax.NatsSubject
import systems.ajax.englishstudytelegrambot.kafka.publisher.EventPublisher
import systems.ajax.englishstudytelegrambot.nats.NatsPublishFactory
import systems.ajax.event.word.UpdateWordEventOuterClass.UpdateWordEvent

@Component
class UpdateWordEventPublisher(
    val natsPublishFactory: NatsPublishFactory,
) : EventPublisher<UpdateWordEvent> {

    override fun publish(message: UpdateWordEvent) {
        natsPublishFactory.publish(createSubject(message.wordSpelling), message.toByteArray())
    }

    private fun createSubject(spelling: String): String =
        buildString {
            append(NatsSubject.Event.GET_WORDS_UPDATES_BY_SPELLING_SUBJECT)
            append(spelling)
        }
}
