package systems.ajax.infrastructure.nats.mapper

import systems.ajax.domain.model.AdditionalInfoAboutWord
import systems.ajax.domain.model.Word
import systems.ajax.entity.WordOuterClass
import systems.ajax.entity.Word as ProtoWord
import systems.ajax.entity.AdditionalInfoAboutWord as ProtoAdditionalInfoAboutWord

fun ProtoWord.toModel(): Word =
    Word(id, spelling, translating, libraryId, additionInfoAboutWord.toModel())

fun ProtoAdditionalInfoAboutWord.toModel(): AdditionalInfoAboutWord =
    AdditionalInfoAboutWord(linkToAudio, definitionOfWord, exampleInSentences, pronunciationOfWord)

fun Word.toProto(): ProtoWord =
    ProtoWord.newBuilder()
        .setId(id)
        .setSpelling(spelling)
        .setTranslating(translate)
        .setLibraryId(libraryId)
        .setAdditionInfoAboutWord(additionalInfoAboutWord.toProto())
        .build()

fun AdditionalInfoAboutWord.toProto(): ProtoAdditionalInfoAboutWord =
    ProtoAdditionalInfoAboutWord.newBuilder()
        .setLinkToAudio(linkToAudio)
        .setDefinitionOfWord(definitionOfWord)
        .setExampleInSentences(exampleInSentences)
        .setPronunciationOfWord(pronunciationOfWord)
        .build()
