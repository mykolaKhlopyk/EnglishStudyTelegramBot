package systems.ajax.englishstudytelegrambot.kafka.receiver

import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver
import systems.ajax.response_request.word.UpdateWordEventOuterClass.UpdateWordEvent

@Component
class UpdateWordEventReceiver(
    val receiver: KafkaReceiver<String, UpdateWordEvent>,
) {

    @PostConstruct
    fun updateWordEventHandler() =
        receiver.receive()
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe { event ->
                val updatedWordEventValue = event.value()
                log.info(
                    "UpdateWordEvent was gotten, word spelling = {}, new translate = {}",
                    updatedWordEventValue.wordSpelling,
                    updatedWordEventValue.newWordTranslate
                )
            }

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
