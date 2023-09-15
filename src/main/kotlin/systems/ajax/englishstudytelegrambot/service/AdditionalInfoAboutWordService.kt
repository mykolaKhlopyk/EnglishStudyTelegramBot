package systems.ajax.englishstudytelegrambot.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import systems.ajax.englishstudytelegrambot.dto.AudioForWordResponse
import systems.ajax.englishstudytelegrambot.dto.DefinitionOfWordResponse
import systems.ajax.englishstudytelegrambot.dto.ExampleOfWordResponse
import systems.ajax.englishstudytelegrambot.dto.PronunciationOfWordResponse
import systems.ajax.englishstudytelegrambot.entity.AdditionalInfoAboutWord
import systems.ajax.englishstudytelegrambot.external.source.ExternalWordSource
import systems.ajax.englishstudytelegrambot.property.WordnikLinkProperties
import kotlin.reflect.KSuspendFunction1

interface AdditionalInfoAboutWordService {
    suspend fun findAdditionInfoAboutWord(wordSpelling: String): AdditionalInfoAboutWord
}

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
        externalWordSource.customGetInfoAboutWord<AudioForWordResponse>(wordSpelling, wordnikLinkProperties.audioSourceLink)

    private suspend fun findDefinitionOfWord(wordSpelling: String): String =
        externalWordSource.customGetInfoAboutWord<DefinitionOfWordResponse>(
            wordSpelling,
            wordnikLinkProperties.definitionOfWord
        )

    private suspend fun findExampleOfWord(wordSpelling: String): String =
        externalWordSource.customGetInfoAboutWord<ExampleOfWordResponse>(wordSpelling, wordnikLinkProperties.examplesOfUsingWordInSentences)

    private suspend fun findPronunciationOfWord(wordSpelling: String): String =
        externalWordSource.customGetInfoAboutWord<PronunciationOfWordResponse>(
            wordSpelling,
            wordnikLinkProperties.correctPronunciationOfWord
        )

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
