package systems.ajax.englishstudytelegrambot.nats.mapper

import entity.LibraryOuterClass
import entity.WordOuterClass
import exception.FailureOuterClass.Failure
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.Word

fun Library.toLibraryResponse(): LibraryOuterClass.Library =
    LibraryOuterClass.Library.newBuilder()
        .setId(id.toHexString())
        .setName(name)
        .setOwnerId(ownerId)
        .build()

fun Word.toWordResponse(): WordOuterClass.Word =
    WordOuterClass.Word.newBuilder()
        .setId(id.toHexString())
        .setLibraryId(libraryId.toHexString())
        .setSpelling(spelling)
        .setTranslating(translate)
        .setAdditionInfoAboutWord(
            additionalInfoAboutWord.let {
                WordOuterClass.AdditionalInfoAboutWord.newBuilder()
                    .setDefinitionOfWord(it.definitionOfWord)
                    .setPronunciationOfWord(it.pronunciationOfWord)
                    .setExampleInSentences(it.exampleInSentences)
                    .setLinkToAudio(it.linkToAudio)
            })
        .build()

fun Throwable.toFailureResponse(): Failure =
    Failure.newBuilder().setErrorMessage(message).build()
