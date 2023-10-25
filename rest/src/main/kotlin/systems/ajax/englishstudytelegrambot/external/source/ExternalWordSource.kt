package systems.ajax.englishstudytelegrambot.external.source

import org.slf4j.LoggerFactory
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
            .doOnNext { value -> log.info("class {}, value {}", T::class, value) }
            .next()
            .map(GettingPartOfAdditionalInfoAboutWord::partOfAdditionalInfoAboutWord)
            .doOnError {
                log.error("additional info for word {} wasn't found, by searching {}", wordSpelling, T::class.java.name)
            }
            .onErrorReturn("Missing")

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
