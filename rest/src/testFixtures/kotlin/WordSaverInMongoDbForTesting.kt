import org.bson.types.ObjectId
import WordFactory.createWord
import systems.ajax.englishstudytelegrambot.repository.WordRepository


object WordSaverInMongoDbForTesting {
    fun WordRepository.saveWordForTesting(
        libraryId: ObjectId,
        spelling: String = "${System.nanoTime()} spelling",
        translate: String = "translate"
    ) = saveNewWord(createWord(libraryId, spelling, translate)).block()!!
}
