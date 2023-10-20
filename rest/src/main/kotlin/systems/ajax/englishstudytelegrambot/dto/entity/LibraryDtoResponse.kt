package systems.ajax.englishstudytelegrambot.dto.entity

import org.bson.types.ObjectId
import systems.ajax.englishstudytelegrambot.entity.Library

data class LibraryDtoResponse(
    val id: ObjectId = ObjectId(),
    val name: String,
)

fun Library.toDtoResponse() =
    LibraryDtoResponse(id, name)
