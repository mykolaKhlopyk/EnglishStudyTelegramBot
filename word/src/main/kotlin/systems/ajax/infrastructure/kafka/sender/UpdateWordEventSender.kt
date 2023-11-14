package systems.ajax.infrastructure.kafka.sender

import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono
import systems.ajax.application.ports.output.UpdateWordEventSenderOut
import systems.ajax.domain.model.Word
import systems.ajax.infrastructure.kafka.config.KafkaTopics
import systems.ajax.response_request.word.UpdateWordEvent

@Component
class UpdateWordEventSender(
    val sender: KafkaSender<String, UpdateWordEvent>,
) : UpdateWordEventSenderOut {

    override fun sendEvent(word: Word, libraryName: String, telegramUserId: String): Mono<Void> =
        sender.send(createRecord(word, libraryName, telegramUserId).toMono()).then()


    private fun createRecord(
        it: Word,
        libraryName: String,
        telegramUserId: String,
    ): SenderRecord<String, UpdateWordEvent, Nothing> =
        SenderRecord.create(
            ProducerRecord(
                KafkaTopics.UPDATED_WORD, it.libraryId,
                UpdateWordEvent.newBuilder()
                    .setLibraryName(libraryName)
                    .setTelegramUserId(telegramUserId)
                    .setWordSpelling(it.spelling)
                    .setNewWordTranslate(it.translate)
                    .build()
            ),
            null
        )
}
