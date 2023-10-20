package systems.ajax.englishstudytelegrambot.dto.entity

import systems.ajax.englishstudytelegrambot.entity.AdditionalInfoAboutWord

data class AdditionalInfoAboutWordDtoResponse(
    val linkToAudio: String,
    val definitionOfWord: String,
    val exampleInSentences: String,
    val pronunciationOfWord: String
)

fun AdditionalInfoAboutWord.toDtoResponse() =
    AdditionalInfoAboutWordDtoResponse(linkToAudio, definitionOfWord, exampleInSentences, pronunciationOfWord)
