package systems.ajax.englishstudytelegrambot.nats.controller

import org.bson.types.ObjectId
import systems.ajax.englishstudytelegrambot.entity.AdditionalInfoAboutWord
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.repository.WordRepository


object WordSaverInDbForTesting {
    fun WordRepository.saveWordForTesting(
        libraryId: ObjectId,
        spelling: String = "${System.nanoTime()} spelling",
        translate: String = "translate"
    ) = saveNewWord(createWord(libraryId, spelling, translate)).block()


    private fun createWord(
        libraryId: ObjectId,
        spelling: String,
        translate: String
    ) = Word(
        spelling = spelling,
        translate = translate,
        libraryId = libraryId,
        additionalInfoAboutWord = createEmptyAdditionalInfoAboutWord()
    )

    private fun createEmptyAdditionalInfoAboutWord() = AdditionalInfoAboutWord("", "", "", "")

}