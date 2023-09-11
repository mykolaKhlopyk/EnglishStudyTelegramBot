package systems.ajax.englishstudytelegrambot.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AudioForWordResponse(
    @JsonProperty("fileUrl") val fileUrl: String
) {
    override fun toString(): String = fileUrl
}

data class DefinitionOfWordResponse(
    @JsonProperty("text") val definition: String
) {
    override fun toString(): String = definition
}

data class ExampleOfWordResponse(
    @JsonProperty("examples") val examples: Array<Example>
) {
    data class Example(@JsonProperty("text") val text: String) {
        override fun toString(): String = text
    }

    override fun toString(): String =
        buildString { examples.forEachIndexed() { index, example -> this.append("$index) $example\n") } }
}

data class PronunciationOfWordResponse(
    @JsonProperty("raw") val pronunciation: String
) {
    override fun toString(): String = pronunciation
}
