package systems.ajax.englishstudytelegrambot.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class WordDto(
    @JsonProperty("spelling") val spelling: String,
    @JsonProperty("translate") val translate: String
)