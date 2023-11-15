package systems.ajax.application.ports.output

import reactor.core.publisher.Mono
import systems.ajax.domain.model.Word

interface UpdateWordEventSenderOutPort {

    fun sendEvent(word: Word, libraryName: String, telegramUserId: String): Mono<Void>
}
