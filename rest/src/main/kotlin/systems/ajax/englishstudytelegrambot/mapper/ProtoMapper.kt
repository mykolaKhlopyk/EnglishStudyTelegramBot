package systems.ajax.englishstudytelegrambot.mapper

import systems.ajax.englishstudytelegrambot.entity.AdditionalInfoAboutWord as MongoAdditionalInfoAboutWord
import systems.ajax.englishstudytelegrambot.entity.Library as MongoLibrary
import systems.ajax.englishstudytelegrambot.entity.Word as MongoWord
import systems.ajax.entity.LibraryOuterClass.Library
import systems.ajax.entity.WordOuterClass.AdditionalInfoAboutWord
import systems.ajax.entity.WordOuterClass.Word


fun MongoWord.toWordResponse(): Word =
    Word.newBuilder()
        .setId(id.toHexString())
        .setSpelling(spelling)
        .setTranslating(translate)
        .setLibraryId(libraryId.toHexString())
        .setAdditionInfoAboutWord(buildAdditionalInfoAboutWord(additionalInfoAboutWord))
        .build()

fun MongoLibrary.toLibraryResponse(): Library =
    Library.newBuilder()
        .setId(id.toHexString())
        .setName(name)
        .setOwnerId(ownerId)
        .build()

private fun buildAdditionalInfoAboutWord(additionalInfoAboutWord: MongoAdditionalInfoAboutWord) =
    additionalInfoAboutWord.let {
        AdditionalInfoAboutWord.newBuilder()
            .setDefinitionOfWord(it.definitionOfWord)
            .setPronunciationOfWord(it.pronunciationOfWord)
            .setExampleInSentences(it.exampleInSentences)
            .setLinkToAudio(it.linkToAudio)
    }
