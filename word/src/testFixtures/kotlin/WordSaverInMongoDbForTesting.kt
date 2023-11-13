import WordFactory.createWord
import systems.ajax.application.ports.output.WordRepositoryOut

object WordSaverInMongoDbForTesting {

    fun WordRepositoryOut.saveWordForTesting(
        libraryId: String,
        spelling: String = "${System.nanoTime()} spelling",
        translate: String = "translate"
    ) = saveNewWord(createWord(libraryId, spelling, translate)).block()!!
}
