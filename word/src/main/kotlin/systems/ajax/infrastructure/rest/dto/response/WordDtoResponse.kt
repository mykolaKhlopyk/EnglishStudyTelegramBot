package systems.ajax.infrastructure.rest.dto.response

import org.bson.types.ObjectId
import systems.ajax.domain.model.Word

data class WordDtoResponse(
    val id: ObjectId = ObjectId(),
    val spelling: String,
    val translate: String,
    val additionalInfoAboutWordDtoResponse: AdditionalInfoAboutWordDtoResponse
)

fun Word.toDtoResponse() =
    WordDtoResponse(ObjectId(id), spelling, translate, additionalInfoAboutWord.toDtoResponse())
