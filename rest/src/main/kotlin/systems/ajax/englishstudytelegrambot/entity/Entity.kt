package systems.ajax.englishstudytelegrambot.entity

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

data class User(
    @Field("ownerId")
    val telegramUserId: String
)

@Document(value = "libraries")
@CompoundIndex(name = "libraryName_owner", def = "{'name' : 1, 'ownerId': 1}", unique = true)
data class Library(
    @Id val id: ObjectId = ObjectId(),
    val name: String,
    val ownerId: String
)

@Document(value = "words")
@CompoundIndex(name = "libraryId_spelling", def = "{'libraryId' : 1, 'spelling': 1}", unique = true)
data class Word(
    @Id
    @JsonSerialize(using = ToStringSerializer::class)   // TODO add separate model for Redis
    val id: ObjectId = ObjectId(),
    val spelling: String,
    val translate: String,
    @JsonSerialize(using = ToStringSerializer::class)   // TODO add separate model for Redis
    val libraryId: ObjectId,
    val additionalInfoAboutWord: AdditionalInfoAboutWord
)

@Serializable
data class AdditionalInfoAboutWord(
    val linkToAudio: String,
    val definitionOfWord: String,
    val exampleInSentences: String,
    val pronunciationOfWord: String
)
