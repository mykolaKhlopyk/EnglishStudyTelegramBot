package systems.ajax.infrastructure.mongo.mapper

import org.bson.types.ObjectId
import systems.ajax.domain.model.AdditionalInfoAboutWord
import systems.ajax.domain.model.Word
import systems.ajax.infrastructure.mongo.entity.MongoAdditionalInfoAboutWord
import systems.ajax.infrastructure.mongo.entity.MongoWord

fun MongoWord.toModel(): Word =
    Word(id.toHexString(), spelling, translate, libraryId.toHexString(), additionalInfoAboutWord.toModel())

fun Word.toMongoEntity(): MongoWord =
    if (id.isBlank())
        MongoWord(ObjectId(), spelling, translate, ObjectId(libraryId), additionalInfoAboutWord.toMongoEntity())
    else
        MongoWord(ObjectId(id), spelling, translate, ObjectId(libraryId), additionalInfoAboutWord.toMongoEntity())

fun MongoAdditionalInfoAboutWord.toModel(): AdditionalInfoAboutWord =
    AdditionalInfoAboutWord(linkToAudio, definitionOfWord, exampleInSentences, pronunciationOfWord)

fun AdditionalInfoAboutWord.toMongoEntity(): MongoAdditionalInfoAboutWord =
    MongoAdditionalInfoAboutWord(linkToAudio, definitionOfWord, exampleInSentences, pronunciationOfWord)
