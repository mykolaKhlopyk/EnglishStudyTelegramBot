package systems.ajax.infrastructure.rest.dto

import org.bson.types.ObjectId
import systems.ajax.domain.model.Library

data class LibraryDtoResponse(
    val id: ObjectId = ObjectId(),
    val name: String,
)

fun Library.toDtoResponse() =
    LibraryDtoResponse(ObjectId(id), name)
