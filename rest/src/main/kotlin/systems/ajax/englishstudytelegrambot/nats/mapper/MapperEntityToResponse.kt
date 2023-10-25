package systems.ajax.englishstudytelegrambot.nats.mapper

import systems.ajax.englishstudytelegrambot.dto.entity.AdditionalInfoAboutWordDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.LibraryDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.WordDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.toDtoResponse
import systems.ajax.englishstudytelegrambot.entity.Library as MongoLibrary
import systems.ajax.englishstudytelegrambot.entity.Word as MongoWord
import systems.ajax.entity.LibraryOuterClass.Library
import systems.ajax.entity.WordOuterClass.AdditionalInfoAboutWord
import systems.ajax.entity.WordOuterClass.Word

fun LibraryDtoResponse.toLibraryResponse(): Library =
    Library.newBuilder()
        .setId(id.toHexString())
        .setName(name)
        .build()

fun WordDtoResponse.toWordResponse(): Word =
    Word.newBuilder()
        .setId(id.toHexString())
        .setSpelling(spelling)
        .setTranslating(translate)
        .setAdditionInfoAboutWord(buildAdditionalInfoAboutWord(additionalInfoAboutWordDtoResponse))
        .build()

fun MongoWord.toWordResponse(): Word =
    toDtoResponse().toWordResponse()

fun MongoLibrary.toLibraryResponse(): Library =
    toDtoResponse().toLibraryResponse()

private fun buildAdditionalInfoAboutWord(additionalInfoAboutWordDtoResponse: AdditionalInfoAboutWordDtoResponse) =
    additionalInfoAboutWordDtoResponse.let {
        AdditionalInfoAboutWord.newBuilder()
            .setDefinitionOfWord(it.definitionOfWord)
            .setPronunciationOfWord(it.pronunciationOfWord)
            .setExampleInSentences(it.exampleInSentences)
            .setLinkToAudio(it.linkToAudio)
    }
