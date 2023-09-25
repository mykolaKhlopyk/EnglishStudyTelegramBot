package systems.ajax.englishstudytelegrambot.repository

import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.exception.WordIsMissing
import systems.ajax.englishstudytelegrambot.exception.WordNotFoundBySpendingException

interface WordRepository {

    fun saveNewWord(word: Word): Word

    fun updateWordTranslating(wordId: String, newWordTranslate: String): Word

    fun deleteWord(wordId: String): Word

    fun isWordBelongsToLibraryByWordId(wordId: String, libraryId: String): Boolean

    fun isWordBelongsToLibraryByWordSpelling(wordSpelling: String, libraryId: String): Boolean

    fun getFullInfoAboutWord(wordId: String): Word

    fun getWordIdBySpellingAndLibraryId(wordSpelling: String, libraryId: String): String

    fun getAllWords(): List<Word>
}

@Repository
class WordRepositoryImpl(
    val mongoTemplate: MongoTemplate
) : WordRepository {

    override fun saveNewWord(word: Word): Word =
        mongoTemplate.save(word)

    override fun updateWordTranslating(wordId: String, newWordTranslate: String): Word =
        mongoTemplate.findAndModify(
            Query.query(Criteria.where("_id").`is`(wordId)),
            Update.update("translate", newWordTranslate),
            FindAndModifyOptions().returnNew(true),
            Word::class.java
        ) ?: throw WordNotFoundBySpendingException()

    override fun deleteWord(wordId: String): Word =
        mongoTemplate.findAndRemove(
            Query.query(Criteria.where("_id").`is`(wordId)),
            Word::class.java
        ) ?: throw WordNotFoundBySpendingException()

    override fun isWordBelongsToLibraryByWordId(wordId: String, libraryId: String): Boolean =
        mongoTemplate.exists(
            Query.query(Criteria.where("libraryId").`is`(libraryId).and("id").`is`(wordId)),
            Word::class.java
        )

    override fun isWordBelongsToLibraryByWordSpelling(wordSpelling: String, libraryId: String): Boolean =
        mongoTemplate.exists(
            Query.query(Criteria.where("libraryId").`is`(libraryId).and("spelling").`is`(wordSpelling)),
            Word::class.java
        )

    override fun getFullInfoAboutWord(wordId: String): Word =
        mongoTemplate.findById(wordId, Word::class.java) ?: throw WordIsMissing()

    override fun getAllWords(): List<Word> =
        mongoTemplate.findAll(Word::class.java)



    override fun getWordIdBySpellingAndLibraryId(wordSpelling: String, libraryId: String): String =
        mongoTemplate.findOne(
            Query.query(Criteria.where("spelling").`is`(wordSpelling).and("libraryId").`is`(libraryId)),
            Word::class.java
        )?.id ?: throw WordIsMissing()
}
