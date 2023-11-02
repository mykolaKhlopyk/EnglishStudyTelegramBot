package systems.ajax.englishstudytelegrambot.service

import reactor.core.publisher.Mono
import systems.ajax.englishstudytelegrambot.entity.AdditionalInfoAboutWord

interface AdditionalInfoAboutWordService {
    fun findAdditionInfoAboutWord(wordSpelling: String): Mono<AdditionalInfoAboutWord>
}
