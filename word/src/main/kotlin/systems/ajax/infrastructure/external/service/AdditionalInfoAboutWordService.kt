package systems.ajax.infrastructure.external.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import reactor.kotlin.core.util.function.component3
import reactor.kotlin.core.util.function.component4
import systems.ajax.application.ports.output.AdditionalInfoAboutWordServiceOut
import systems.ajax.domain.model.AdditionalInfoAboutWord
import systems.ajax.infrastructure.external.api.AudioForWordResponse
import systems.ajax.infrastructure.external.api.DefinitionOfWordResponse
import systems.ajax.infrastructure.external.api.ExampleOfWordResponse
import systems.ajax.infrastructure.external.api.PronunciationOfWordResponse
import systems.ajax.infrastructure.external.source.ExternalWordSource
import systems.ajax.infrastructure.property.WordnikProperties

@Service
class AdditionalInfoAboutWordService(
    private val wordnikProperties: WordnikProperties,
    private val externalWordSource: ExternalWordSource
) : AdditionalInfoAboutWordServiceOut {

    @Suppress("DestructuringDeclarationWithTooManyEntries")
    override fun findAdditionInfoAboutWord(wordSpelling: String): Mono<AdditionalInfoAboutWord> =
        Mono.zip(
            wordSpelling.findAudioLink(),
            wordSpelling.findDefinitionOfWord(),
            wordSpelling.findExampleOfWord(),
            wordSpelling.findPronunciationOfWord()
        ).map { (audio, definition, example, pronunciation) ->
            AdditionalInfoAboutWord(audio, definition, example, pronunciation)
        }

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
