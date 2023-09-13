package systems.ajax.englishstudytelegrambot.service

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ResponseStatusException
import systems.ajax.englishstudytelegrambot.dto.AudioForWordResponse
import systems.ajax.englishstudytelegrambot.dto.DefinitionOfWordResponse
import systems.ajax.englishstudytelegrambot.dto.ExampleOfWordResponse
import systems.ajax.englishstudytelegrambot.dto.PronunciationOfWordResponse
import systems.ajax.englishstudytelegrambot.dto.GettingPartOfAdditionalInfoAboutWord
import systems.ajax.englishstudytelegrambot.entity.AdditionalInfoAboutWord
import kotlin.reflect.KSuspendFunction1

interface AdditionalInfoAboutWordService {
    suspend fun findAdditionInfoAboutWord(wordSpelling: String): AdditionalInfoAboutWord
}

@ConfigurationProperties(prefix = "link")
data class WordnikLinkProperties @ConstructorBinding constructor(
    val audio: String,
    val definition: String,
    val example: String,
    val pronunciation: String
)

@Service
class AdditionalInfoAboutWordServiceImpl(
    private val wordnikLinkProperties: WordnikLinkProperties,
    private val externalWordSource: ExternalWordSource
) : AdditionalInfoAboutWordService {

    override suspend fun findAdditionInfoAboutWord(wordSpelling: String): AdditionalInfoAboutWord {
        val audioLink: String = getPartOfInfoOrElseReturnMissing(wordSpelling, ::findAudioLink)
        val definition: String = getPartOfInfoOrElseReturnMissing(wordSpelling, ::findDefinitionOfWord)
        val example: String = getPartOfInfoOrElseReturnMissing(wordSpelling, ::findExampleOfWord)
        val pronunciation: String = getPartOfInfoOrElseReturnMissing(wordSpelling, ::findPronunciationOfWord)

        return AdditionalInfoAboutWord(audioLink, definition, example, pronunciation)
    }

    private suspend fun getPartOfInfoOrElseReturnMissing(
        wordSpelling: String,
        findFunction: KSuspendFunction1<String, String>
    ): String =
        try {
            findFunction(wordSpelling)
        } catch (e: ResponseStatusException) {
            log.error(e.toString())
            "Missing"
        }

    private suspend fun findAudioLink(wordSpelling: String): String =
        externalWordSource.customGetInfoAboutWord<AudioForWordResponse>(wordSpelling, wordnikLinkProperties.audio)

    private suspend fun findDefinitionOfWord(wordSpelling: String): String =
        externalWordSource.customGetInfoAboutWord<DefinitionOfWordResponse>(
            wordSpelling,
            wordnikLinkProperties.definition
        )

    private suspend fun findExampleOfWord(wordSpelling: String): String =
        externalWordSource.customGetInfoAboutWord<ExampleOfWordResponse>(wordSpelling, wordnikLinkProperties.example)

    private suspend fun findPronunciationOfWord(wordSpelling: String): String =
        externalWordSource.customGetInfoAboutWord<PronunciationOfWordResponse>(
            wordSpelling,
            wordnikLinkProperties.pronunciation
        )

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}

@ConfigurationProperties(prefix = "wordnik.api")
data class WordinkKeyProperty @ConstructorBinding constructor(val key: String)

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
