package systems.ajax.englishstudytelegrambot.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.dto.AudioForWordResponse
import systems.ajax.englishstudytelegrambot.dto.DefinitionOfWordResponse
import systems.ajax.englishstudytelegrambot.dto.ExampleOfWordResponse
import systems.ajax.englishstudytelegrambot.dto.PronunciationOfWordResponse
import systems.ajax.englishstudytelegrambot.entity.AdditionalInfoAboutWord
import systems.ajax.englishstudytelegrambot.external.source.ExternalWordSource
import systems.ajax.englishstudytelegrambot.property.WordnikLinkProperties

interface AdditionalInfoAboutWordService {
    suspend fun findAdditionInfoAboutWord(wordSpelling: String): AdditionalInfoAboutWord
}

@Service
class AdditionalInfoAboutWordServiceImpl(
    private val wordnikLinkProperties: WordnikLinkProperties,
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
            wordnikLinkProperties.audioSourceLink
        )

    private suspend fun String.findDefinitionOfWord(): String =
        externalWordSource.customGetInfoAboutWord<DefinitionOfWordResponse>(
            this,
            wordnikLinkProperties.definitionOfWordLink
        )

    private suspend fun String.findExampleOfWord(): String =
        externalWordSource.customGetInfoAboutWord<ExampleOfWordResponse>(
            this,
            wordnikLinkProperties.examplesOfUsingWordInSentencesLink
        )

    private suspend fun String.findPronunciationOfWord(): String =
        externalWordSource.customGetInfoAboutWord<PronunciationOfWordResponse>(
            this,
            wordnikLinkProperties.correctPronunciationOfWordLink
        )

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
