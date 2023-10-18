package systems.ajax.englishstudytelegrambot.nats.mapper

import systems.ajax.englishstudytelegrambot.entity.MongoLibrary
import systems.ajax.englishstudytelegrambot.entity.MongoWord
import systems.ajax.entity.Library
import systems.ajax.entity.WordOuterClass

fun MongoLibrary.toLibraryResponse(): Library =
    Library.newBuilder()
        .setId(id.toHexString())
        .setName(name)
        .setOwnerId(ownerId)
        .build()

fun MongoWord.toWordResponse(): WordOuterClass.Word =
    WordOuterClass.Word.newBuilder()
        .setId(id.toHexString())
        .setLibraryId(libraryId.toHexString())
        .setSpelling(spelling)
        .setTranslating(translate)
        .setAdditionInfoAboutWord(
            mongoAdditionalInfoAboutWord.let {
                WordOuterClass.AdditionalInfoAboutWord.newBuilder()
                    .setDefinitionOfWord(it.definitionOfWord)
                    .setPronunciationOfWord(it.pronunciationOfWord)
                    .setExampleInSentences(it.exampleInSentences)
                    .setLinkToAudio(it.linkToAudio)
            })
        .build()
