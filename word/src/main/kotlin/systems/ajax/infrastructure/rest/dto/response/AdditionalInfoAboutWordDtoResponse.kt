package systems.ajax.infrastructure.rest.dto.response

import systems.ajax.domain.model.AdditionalInfoAboutWord

data class AdditionalInfoAboutWordDtoResponse(
    val linkToAudio: String,
    val definitionOfWord: String,
    val exampleInSentences: String,
    val pronunciationOfWord: String
)

fun AdditionalInfoAboutWord.toDtoResponse() =
    AdditionalInfoAboutWordDtoResponse(linkToAudio, definitionOfWord, exampleInSentences, pronunciationOfWord)
