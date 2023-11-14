package systems.ajax.application.ports.output

import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import systems.ajax.domain.model.Word
import systems.ajax.response_request.word.UpdateWordEvent

interface UpdateWordEventSenderOut {

    fun sendEvent(word: Word, libraryName: String, telegramUserId: String): Mono<Void>
}
