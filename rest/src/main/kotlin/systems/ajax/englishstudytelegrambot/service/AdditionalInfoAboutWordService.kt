package systems.ajax.englishstudytelegrambot.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.dto.external.api.AudioForWordResponse
import systems.ajax.englishstudytelegrambot.dto.external.api.DefinitionOfWordResponse
import systems.ajax.englishstudytelegrambot.dto.external.api.ExampleOfWordResponse
import systems.ajax.englishstudytelegrambot.dto.external.api.PronunciationOfWordResponse
import systems.ajax.englishstudytelegrambot.entity.AdditionalInfoAboutWord
import systems.ajax.englishstudytelegrambot.external.source.ExternalWordSource
import systems.ajax.englishstudytelegrambot.property.WordnikProperties

interface AdditionalInfoAboutWordService {
    suspend fun findAdditionInfoAboutWord(wordSpelling: String): AdditionalInfoAboutWord
}

@Service
class AdditionalInfoAboutWordServiceImpl(
    private val wordnikProperties: WordnikProperties,
    private val externalWordSource: ExternalWordSource
) : AdditionalInfoAboutWordService {

    override suspend fun findAdditionInfoAboutWord(wordSpelling: String): AdditionalInfoAboutWord {
        val audioLink: String = wordSpelling.findAudioLink()
        val definition: String = wordSpelling.findDefinitionOfWord()
        val exampleInSentences: String = wordSpelling.findExampleOfWord()
        val pronunciation: String = wordSpelling.findPronunciationOfWord()

        return AdditionalInfoAboutWord(audioLink, definition, exampleInSentences, pronunciation)
    }

    private suspend fun String.findAudioLink(): String =
        externalWordSource.customGetInfoAboutWord<AudioForWordResponse>(
            this,
            wordnikProperties.link.audioSourceLink
        )

    private suspend fun String.findDefinitionOfWord(): String =
        externalWordSource.customGetInfoAboutWord<DefinitionOfWordResponse>(
            this,
            wordnikProperties.link.definitionOfWordLink
        )

    private suspend fun String.findExampleOfWord(): String =
        externalWordSource.customGetInfoAboutWord<ExampleOfWordResponse>(
            this,
            wordnikProperties.link.examplesOfUsingWordInSentencesLink
        )

    private suspend fun String.findPronunciationOfWord(): String =
        externalWordSource.customGetInfoAboutWord<PronunciationOfWordResponse>(
            this,
            wordnikProperties.link.correctPronunciationOfWordLink
        )

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
