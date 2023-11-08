package systems.ajax.englishstudytelegrambot.kafka.listener

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import systems.ajax.englishstudytelegrambot.kafka.config.KafkaTopics
import systems.ajax.response_request.word.EventUpdateWordOuterClass.EventUpdateWord

@Component
class KafkaListener {

    @KafkaListener(
        topics = [KafkaTopics.UPDATED_WORD],
        groupId = "group_id"
    )
    fun listener(record: ConsumerRecord<String, EventUpdateWord>) {
        val message = record.value()
        log.info("word with spelling '{}', was updated, new translate '{}'", message.wordSpelling, message.newWordTranslate)
    }

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
