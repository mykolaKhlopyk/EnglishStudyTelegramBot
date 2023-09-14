package systems.ajax.englishstudytelegrambot.external.source

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ResponseStatusException
import systems.ajax.englishstudytelegrambot.dto.GettingPartOfAdditionalInfoAboutWord
import systems.ajax.englishstudytelegrambot.property.WordinkKeyProperty

@Component
class ExternalWordSource(val webClient: WebClient, val wordnikKeyProperty: WordinkKeyProperty) {

    final inline fun <reified T : GettingPartOfAdditionalInfoAboutWord> customGetInfoAboutWord(
        wordSpelling: String,
        link: String
    ): String =
        webClient.get()
            .uri(
                link, wordSpelling, wordnikKeyProperty.key
            )
            .retrieve()
            .onStatus({ responseStatus ->
                responseStatus == HttpStatus.NOT_FOUND
            }) { throw ResponseStatusException(HttpStatus.NOT_FOUND) }
            .bodyToFlux(T::class.java)
            .blockFirst()
            ?.partOfAdditionalInfoAboutWord ?: "Missing"
}
