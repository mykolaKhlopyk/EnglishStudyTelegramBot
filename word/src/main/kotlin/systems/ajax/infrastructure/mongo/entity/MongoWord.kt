package systems.ajax.infrastructure.mongo.entity

import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document

@Document(value = "words")
@CompoundIndex(name = "libraryId_spelling", def = "{'libraryId' : 1, 'spelling': 1}", unique = true)
data class MongoWord(
    @Id
    val id: ObjectId = ObjectId(),
    val spelling: String,
    val translate: String,
    val libraryId: ObjectId,
    val additionalInfoAboutWord: MongoAdditionalInfoAboutWord,
)

@Serializable
data class MongoAdditionalInfoAboutWord(
    val linkToAudio: String,
    val definitionOfWord: String,
    val exampleInSentences: String,
    val pronunciationOfWord: String,
)
