package systems.ajax.englishstudytelegrambot.dto.entity

import systems.ajax.englishstudytelegrambot.entity.User

data class UserDtoResponse(
    val telegramUserId: String
)

fun User.toDtoResponse() =
    UserDtoResponse(telegramUserId)
