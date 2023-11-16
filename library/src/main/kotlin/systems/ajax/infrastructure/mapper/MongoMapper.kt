package systems.ajax.infrastructure.mapper

import org.bson.types.ObjectId
import systems.ajax.domain.model.Library
import systems.ajax.infrastructure.mongo.entity.MongoLibrary

fun MongoLibrary.toModel(): Library =
    Library(id.toHexString(), name, ownerId)

fun Library.toMongoEntity(): MongoLibrary =
    MongoLibrary(ObjectId(id), name, ownerId)
