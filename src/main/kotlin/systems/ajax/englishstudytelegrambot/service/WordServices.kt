package systems.ajax.englishstudytelegrambot.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
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

@Service
class AdditionalInfoAboutWordServiceImpl(private val webClient: WebClient) : AdditionalInfoAboutWordService {

    @Value("\${wordnik.api.key}")
    lateinit var wordnikAPIKey: String

    @Value("\${link.audio}")
    lateinit var audioLink: String

    @Value("\${link.definition}")
    lateinit var definitionLink: String

    @Value("\${link.example}")
    lateinit var exampleLink: String

    @Value("\${link.pronunciation}")
    lateinit var pronunciationLink: String

    val log = LoggerFactory.getLogger(this::class.java)

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
        customGetInfoAboutWord<AudioForWordResponse>(wordSpelling, audioLink)

    private suspend fun findDefinitionOfWord(wordSpelling: String): String =
        customGetInfoAboutWord<DefinitionOfWordResponse>(wordSpelling, definitionLink)

    private suspend fun findExampleOfWord(wordSpelling: String): String =
        customGetInfoAboutWord<ExampleOfWordResponse>(wordSpelling, exampleLink)

    private suspend fun findPronunciationOfWord(wordSpelling: String): String =
        customGetInfoAboutWord<PronunciationOfWordResponse>(wordSpelling, pronunciationLink)

    private suspend inline fun <reified T : GettingPartOfAdditionalInfoAboutWord> customGetInfoAboutWord(
        wordSpelling: String,
        link: String
    ): String =
        webClient.get()
            .uri(
                link, wordSpelling, wordnikAPIKey
            )
            .retrieve()
            .onStatus({ responseStatus ->
                responseStatus == HttpStatus.NOT_FOUND
            }) { throw ResponseStatusException(HttpStatus.NOT_FOUND) }
            .bodyToFlux(T::class.java)
            .blockFirst()
            ?.getPartOfAdditionalInfoAboutWord() ?: "Missing"
}
