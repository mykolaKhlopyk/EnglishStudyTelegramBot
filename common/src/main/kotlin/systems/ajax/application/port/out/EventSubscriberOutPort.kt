package systems.ajax.application.port.out

import com.google.protobuf.GeneratedMessageV3
import reactor.core.publisher.Flux

interface EventSubscriberOutPort<EventType : GeneratedMessageV3> {

    fun subscribe(eventKey: String): Flux<EventType>
}
