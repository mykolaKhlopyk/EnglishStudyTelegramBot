package systems.ajax.englishstudytelegrambot.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import systems.ajax.englishstudytelegrambot.dto.external.api.AudioForWordResponse
import systems.ajax.englishstudytelegrambot.dto.external.api.DefinitionOfWordResponse
import systems.ajax.englishstudytelegrambot.dto.external.api.ExampleOfWordResponse
import systems.ajax.englishstudytelegrambot.dto.external.api.PronunciationOfWordResponse
import systems.ajax.englishstudytelegrambot.entity.AdditionalInfoAboutWord
import systems.ajax.englishstudytelegrambot.external.source.ExternalWordSource
import systems.ajax.englishstudytelegrambot.property.WordnikProperties

interface AdditionalInfoAboutWordService {
    fun findAdditionInfoAboutWord(wordSpelling: String): Mono<AdditionalInfoAboutWord>
}

@Service
class AdditionalInfoAboutWordServiceImpl(
    private val wordnikProperties: WordnikProperties,
    private val externalWordSource: ExternalWordSource
) : AdditionalInfoAboutWordService {

    override fun findAdditionInfoAboutWord(wordSpelling: String): Mono<AdditionalInfoAboutWord> =
        Mono.zip(
            wordSpelling.findAudioLink(),
            wordSpelling.findDefinitionOfWord(),
            wordSpelling.findExampleOfWord(),
            wordSpelling.findPronunciationOfWord()
        ).map { additionalInfoAboutWord -> additionalInfoAboutWord.run { AdditionalInfoAboutWord(t1, t2, t3, t4) } }

    private fun String.findAudioLink(): Mono<String> =
        externalWordSource.customGetInfoAboutWordFromWordnikAPI<AudioForWordResponse>(
            this,
            wordnikProperties.link.audioSourceLink
        )

    private fun String.findDefinitionOfWord(): Mono<String> =
        externalWordSource.customGetInfoAboutWordFromWordnikAPI<DefinitionOfWordResponse>(
            this,
            wordnikProperties.link.definitionOfWordLink
        )

    private fun String.findExampleOfWord(): Mono<String> =
        externalWordSource.customGetInfoAboutWordFromWordnikAPI<ExampleOfWordResponse>(
            this,
            wordnikProperties.link.examplesOfUsingWordInSentencesLink
        )

    private fun String.findPronunciationOfWord(): Mono<String> =
        externalWordSource.customGetInfoAboutWordFromWordnikAPI<PronunciationOfWordResponse>(
            this,
            wordnikProperties.link.correctPronunciationOfWordLink
        )
}
