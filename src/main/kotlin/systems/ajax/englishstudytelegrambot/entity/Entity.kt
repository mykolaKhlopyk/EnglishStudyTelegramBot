package systems.ajax.englishstudytelegrambot.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(value = "users")
data class User(
    @Id val telegramUserId: String
)

@Document(value = "libraries")
data class Library(
    @Id val id: String = ObjectId().toHexString(),
    val name: String,
    val ownerId: String,
)

@Document(value = "words")
data class Word(
    @Id val id: String = ObjectId().toHexString(),
    val spelling: String,
    val translate: String,
    val libraryId: String,
    val additionalInfoAboutWord: AdditionalInfoAboutWord
)

data class AdditionalInfoAboutWord(
    val linkToAudio: String,
    val definitionOfWord: String,
    val exampleInSentences: String,
    val pronunciationOfWord: String
)
