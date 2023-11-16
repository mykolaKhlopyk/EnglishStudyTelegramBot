package systems.ajax.infrastructure.external.api

import com.fasterxml.jackson.annotation.JsonProperty

interface GettingPartOfAdditionalInfoAboutWord {

    val partOfAdditionalInfoAboutWord: String
}

data class AudioForWordResponse(
    @JsonProperty("fileUrl") val fileUrl: String
) : GettingPartOfAdditionalInfoAboutWord {

    override val partOfAdditionalInfoAboutWord: String
        get() = fileUrl
}

data class DefinitionOfWordResponse(
    @JsonProperty("text") val definition: String
) : GettingPartOfAdditionalInfoAboutWord {

    override val partOfAdditionalInfoAboutWord: String
        get() = definition
}

data class ExampleOfWordResponse(
    @JsonProperty("examples") val examples: Array<Example>
) : GettingPartOfAdditionalInfoAboutWord {

    data class Example(@JsonProperty("text") val text: String) {
        override fun toString(): String = text
    }

    override val partOfAdditionalInfoAboutWord: String
        get() = buildString { examples.forEachIndexed { index, example -> this.append("$index) $example\n") } }
}

data class PronunciationOfWordResponse(
    @JsonProperty("raw") val pronunciation: String
) : GettingPartOfAdditionalInfoAboutWord {

    override val partOfAdditionalInfoAboutWord: String
        get() = pronunciation
}
