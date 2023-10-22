package systems.ajax.englishstudytelegrambot.external.source

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import systems.ajax.englishstudytelegrambot.dto.external.api.GettingPartOfAdditionalInfoAboutWord
import systems.ajax.englishstudytelegrambot.property.WordnikProperties

@Component
class ExternalWordSource(val wordnikProperties: WordnikProperties, val webClient: WebClient) {

    final inline fun <reified T : GettingPartOfAdditionalInfoAboutWord> customGetInfoAboutWordFromWordnikAPI(
        wordSpelling: String,
        link: String
    ): Mono<String> =
        webClient.get()
            .uri(
                link, wordSpelling, wordnikProperties.tokenKey
            )
            .retrieve()
            .bodyToFlux(T::class.java)
            .next()
            .map(GettingPartOfAdditionalInfoAboutWord::partOfAdditionalInfoAboutWord)
            .onErrorReturn("Missing")
}
