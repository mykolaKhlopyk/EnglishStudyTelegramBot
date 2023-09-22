package systems.ajax.englishstudytelegrambot.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramUserIdDto(
    @JsonProperty("telegramUserId") val telegramUserId: String
)