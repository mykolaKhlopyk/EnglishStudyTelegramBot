package systems.ajax.application.port.out

import com.google.protobuf.GeneratedMessageV3

interface EventPublisherOutPort<EventType : GeneratedMessageV3> {

    fun publish(message: EventType)
}
