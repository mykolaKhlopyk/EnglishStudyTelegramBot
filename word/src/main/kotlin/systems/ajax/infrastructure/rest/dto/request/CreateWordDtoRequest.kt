package systems.ajax.infrastructure.rest.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateWordDtoRequest(
    @JsonProperty("spelling") val spelling: String,
    @JsonProperty("translate") val translate: String
)
