package systems.ajax.englishstudytelegrambot.external.source

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ResponseStatusException
import systems.ajax.englishstudytelegrambot.dto.external.api.GettingPartOfAdditionalInfoAboutWord
import systems.ajax.englishstudytelegrambot.property.WordnikProperties

@Component
class ExternalWordSource(val wordnikProperties: WordnikProperties, val webClient: WebClient) {

    final inline fun <reified T : GettingPartOfAdditionalInfoAboutWord> customGetInfoAboutWord(
        wordSpelling: String,
        link: String
    ): String {
        return runCatching {
            webClient.get()
                .uri(
                    link, wordSpelling, wordnikProperties.tokenKey
                )
                .retrieve()
                .onStatus({ responseStatus ->
                    responseStatus == HttpStatus.NOT_FOUND
                }) { throw ResponseStatusException(HttpStatus.NOT_FOUND) }
                .bodyToFlux(T::class.java)
                .blockFirst()!!
                .partOfAdditionalInfoAboutWord
        }.getOrElse {
            log.info("additional info for word {} isn't found", wordSpelling)
            "Missing"
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
