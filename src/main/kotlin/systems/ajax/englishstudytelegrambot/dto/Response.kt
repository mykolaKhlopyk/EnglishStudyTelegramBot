package systems.ajax.englishstudytelegrambot.dto

import com.fasterxml.jackson.annotation.JsonProperty

interface GettingPartOfAdditionalInfoAboutWord {

    fun getPartOfAdditionalInfoAboutWord(): String

}

data class AudioForWordResponse(
    @JsonProperty("fileUrl") val fileUrl: String
) : GettingPartOfAdditionalInfoAboutWord {

    override fun getPartOfAdditionalInfoAboutWord(): String = fileUrl

}

data class DefinitionOfWordResponse(
    @JsonProperty("text") val definition: String
) : GettingPartOfAdditionalInfoAboutWord {

    override fun getPartOfAdditionalInfoAboutWord(): String = definition

}

data class ExampleOfWordResponse(
    @JsonProperty("examples") val examples: Array<Example>
) : GettingPartOfAdditionalInfoAboutWord {

    data class Example(@JsonProperty("text") val text: String) {
        override fun toString(): String = text
    }

    override fun getPartOfAdditionalInfoAboutWord(): String =
        buildString { examples.forEachIndexed { index, example -> this.append("$index) $example\n") } }

}

data class PronunciationOfWordResponse(
    @JsonProperty("raw") val pronunciation: String
) : GettingPartOfAdditionalInfoAboutWord {

    override fun getPartOfAdditionalInfoAboutWord(): String = pronunciation

}
