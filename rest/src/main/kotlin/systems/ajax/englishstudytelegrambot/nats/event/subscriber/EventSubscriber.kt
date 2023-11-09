package systems.ajax.englishstudytelegrambot.nats.event.subscriber

import com.google.protobuf.GeneratedMessageV3
import reactor.core.publisher.Flux

interface EventSubscriber<EventType : GeneratedMessageV3> {

    fun subscribe(eventKey: String): Flux<EventType>
}
