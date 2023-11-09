package systems.ajax.englishstudytelegrambot.kafka.publisher

import com.google.protobuf.GeneratedMessageV3

interface EventPublisher<EventType : GeneratedMessageV3> {

    fun publish(message: EventType)
}
