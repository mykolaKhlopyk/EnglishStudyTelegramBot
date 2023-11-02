import org.bson.types.ObjectId
import systems.ajax.englishstudytelegrambot.entity.AdditionalInfoAboutWord
import systems.ajax.englishstudytelegrambot.entity.Word

object WordFactory {
    fun createWord(
        libraryId: ObjectId,
        spelling: String = "${System.nanoTime()} spelling",
        translate: String = "translate"
    ) = Word(
        spelling = spelling,
        translate = translate,
        libraryId = libraryId,
        additionalInfoAboutWord = createEmptyAdditionalInfoAboutWord()
    )

    private fun createEmptyAdditionalInfoAboutWord() = AdditionalInfoAboutWord("", "", "", "")

}