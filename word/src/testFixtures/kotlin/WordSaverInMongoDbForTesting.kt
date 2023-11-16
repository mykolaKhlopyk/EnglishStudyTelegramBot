import WordFactory.createWord
import systems.ajax.application.ports.output.WordRepositoryOutPort

object WordSaverInMongoDbForTesting {

    fun WordRepositoryOutPort.saveWordForTesting(
        libraryId: String,
        spelling: String = "${System.nanoTime()} spelling",
        translate: String = "translate"
    ) = saveNewWord(createWord(libraryId, spelling, translate)).block()!!
}
