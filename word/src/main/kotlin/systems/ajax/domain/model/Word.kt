package systems.ajax.domain.model

data class Word(
    val id: String,
    val spelling: String,
    val translate: String,
    val libraryId: String,
    val additionalInfoAboutWord: AdditionalInfoAboutWord
)
