package systems.ajax.englishstudytelegrambot.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(value = "users")
data class User(
    @Id val telegramId: String)

@Document(value = "libraries")
data class Library(
    @Id val id: String = ObjectId().toHexString(),
    val name: String,
    val ownerId: String,
    val words: List<Word> = mutableListOf()
)

data class Word(
    val spelling: String,
    val translate: String,
    val additionalInfoAboutWord: AdditionalInfoAboutWord
)

data class AdditionalInfoAboutWord(
    val linkToAudio: String,
    val definitionOfWord: String,
    val exampleInSentences: String,
    val pronunciationOfWord: String
)
