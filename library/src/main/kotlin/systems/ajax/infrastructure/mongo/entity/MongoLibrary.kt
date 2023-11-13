package systems.ajax.infrastructure.mongo.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(value = "libraries")
@CompoundIndex(name = "libraryName_owner", def = "{'name' : 1, 'ownerId': 1}", unique = true)
data class MongoLibrary(
    @Id val id: ObjectId = ObjectId(),
    val name: String,
    val ownerId: String
)

data class User(
    @Field("ownerId")
    val telegramUserId: String
)
