package systems.ajax.englishstudytelegrambot.kafka.listener

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.MessageHeaders
import org.springframework.stereotype.Component
import systems.ajax.response_request.word.EventUpdateWordOuterClass.EventUpdateWord

@Component
class KafkaListener {

    @KafkaListener(
        topics = ["mytopic"],
        groupId = "group_id",
//contentTypeConverter = Serializer
    )
    fun listener(record: ConsumerRecord<String, EventUpdateWord>) {
        // Log the message and its class
        // Log the message and its class
        log.info("Received message: ${record.value().javaClass}")
        log.info("word with spelling '{}', was updated, new translate '{}'", record.key(), record.value())
        log.info("success '{}'", record.value().wordSpelling)
    }

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
