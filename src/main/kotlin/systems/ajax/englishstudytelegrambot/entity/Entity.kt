package systems.ajax.englishstudytelegrambot.entity

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Document(value = "users")
data class User(val telegramId: String, val username: String, val libraries: List<Library>)

@Document(value = "libraries")
data class Library(val id: String = ObjectId().toHexString(), val name: String, val words: List<Word>)

data class Word(val spelling: String, val translate: String, val additionalInfoAboutWord: AdditionalInfoAboutWord)

data class AdditionalInfoAboutWord(
    val linkToAudio: String,
    val definitionOfWord: String,
    val exampleInSentences: String,
    val pronunciationOfWord: String
)
