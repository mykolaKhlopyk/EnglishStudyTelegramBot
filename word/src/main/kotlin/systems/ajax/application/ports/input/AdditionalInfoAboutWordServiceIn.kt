package systems.ajax.application.ports.input

import reactor.core.publisher.Mono
import systems.ajax.domain.model.AdditionalInfoAboutWord

interface AdditionalInfoAboutWordServiceIn {
    fun findAdditionInfoAboutWord(wordSpelling: String): Mono<AdditionalInfoAboutWord>
}
