package systems.ajax.englishstudytelegrambot.dto.entity

import org.bson.types.ObjectId
import systems.ajax.englishstudytelegrambot.entity.Word

data class WordDtoResponse(
    val id: ObjectId = ObjectId(),
    val spelling: String,
    val translate: String,
    val additionalInfoAboutWordDtoResponse: AdditionalInfoAboutWordDtoResponse
)

fun Word.toDtoResponse() =
    WordDtoResponse(id, spelling, translate, additionalInfoAboutWord.toDtoResponse())
