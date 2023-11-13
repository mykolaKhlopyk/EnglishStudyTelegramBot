import org.bson.types.ObjectId
import systems.ajax.domain.model.AdditionalInfoAboutWord
import systems.ajax.domain.model.Word

object WordFactory {

    fun createWord(
        libraryId: String,
        spelling: String = "${System.nanoTime()} spelling",
        translate: String = "translate",
    ) = Word(
        ObjectId().toHexString(),
        spelling,
        translate,
        libraryId,
        createEmptyAdditionalInfoAboutWord()
    )

    private fun createEmptyAdditionalInfoAboutWord() = AdditionalInfoAboutWord("", "", "", "")
}
