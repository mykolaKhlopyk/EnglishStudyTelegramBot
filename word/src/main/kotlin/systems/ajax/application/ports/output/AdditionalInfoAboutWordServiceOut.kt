package systems.ajax.application.ports.output

import reactor.core.publisher.Mono
import systems.ajax.domain.model.AdditionalInfoAboutWord

interface AdditionalInfoAboutWordServiceOut {
    fun findAdditionInfoAboutWord(wordSpelling: String): Mono<AdditionalInfoAboutWord>
}
