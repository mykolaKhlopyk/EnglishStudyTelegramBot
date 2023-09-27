package systems.ajax.englishstudytelegrambot.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.aggregation.LookupOperation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.exception.WordIsMissing
import systems.ajax.englishstudytelegrambot.exception.WordNotFoundBySpendingException


interface WordRepository {

    fun saveNewWord(word: Word): Word

    fun updateWordTranslating(wordId: ObjectId, newWordTranslate: String): Word

    fun deleteWord(wordId: ObjectId): Word

    fun isWordBelongsToLibraryByWordId(wordId: ObjectId, libraryId: ObjectId): Boolean

    fun isWordBelongsToLibraryByWordSpelling(wordSpelling: String, libraryId: ObjectId): Boolean

    fun getWord(wordId: ObjectId): Word

    fun getWordIdBySpellingAndLibraryId(wordSpelling: String, libraryId: ObjectId): ObjectId

    fun getAllWords(): List<Word>

    fun getWordByLibraryNameTelegramUserIdWordSpelling(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): Word

    fun getWordIdByLibraryNameTelegramUserIdWordSpelling(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): ObjectId
}

@Repository
class WordRepositoryImpl(
    val mongoTemplate: MongoTemplate
) : WordRepository {

    override fun saveNewWord(word: Word): Word =
        mongoTemplate.save(word)

    override fun updateWordTranslating(wordId: ObjectId, newWordTranslate: String): Word =
        mongoTemplate.findAndModify(
            Query.query(Criteria.where("_id").`is`(wordId)),
            Update.update("translate", newWordTranslate),
            FindAndModifyOptions().returnNew(true),
            Word::class.java
        ) ?: throw WordNotFoundBySpendingException()

    override fun deleteWord(wordId: ObjectId): Word =
        mongoTemplate.findAndRemove(
            Query.query(Criteria.where("_id").`is`(wordId)),
            Word::class.java
        ) ?: throw WordNotFoundBySpendingException()

    override fun isWordBelongsToLibraryByWordId(wordId: ObjectId, libraryId: ObjectId): Boolean =
        mongoTemplate.exists(
            Query.query(Criteria.where("libraryId").`is`(libraryId).and("id").`is`(wordId)),
            Word::class.java
        )

    override fun isWordBelongsToLibraryByWordSpelling(wordSpelling: String, libraryId: ObjectId): Boolean =
        mongoTemplate.exists(
            Query.query(Criteria.where("libraryId").`is`(libraryId).and("spelling").`is`(wordSpelling)),
            Word::class.java
        )

    override fun getWord(wordId: ObjectId): Word =
        mongoTemplate.findById(wordId, Word::class.java) ?: throw WordIsMissing()

    override fun getAllWords(): List<Word> =
        mongoTemplate.findAll(Word::class.java)

    override fun getWordIdBySpellingAndLibraryId(wordSpelling: String, libraryId: ObjectId): ObjectId =
        mongoTemplate.findOne(
            Query.query(Criteria.where("spelling").`is`(wordSpelling).and("libraryId").`is`(libraryId)),
            Word::class.java
        )?.id ?: throw WordIsMissing()

    override fun getWordByLibraryNameTelegramUserIdWordSpelling(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): Word {
        val matchLibraryByNameAndOwner = Aggregation.match(
            Criteria.where("name").`is`(libraryName).and("ownerId").`is`(telegramUserId)
        )
        val project: AggregationOperation = Aggregation.project("_id")
        val lookup = LookupOperation.newLookup()
            .from("words")
            .localField("_id")
            .foreignField("libraryId")
            .`as`("wordsLibraries")
        val unwind = Aggregation.unwind("wordsLibraries")
        val replaceRoot = Aggregation.replaceRoot().withValueOf("wordsLibraries")
        val matchWordBySpelling = Aggregation.match(Criteria.where("spelling").`is`(wordSpelling))
        val aggregation = Aggregation.newAggregation(
            matchLibraryByNameAndOwner,
            project,
            lookup,
            unwind,
            replaceRoot,
            matchWordBySpelling
        )
        val result = mongoTemplate.aggregate(
            aggregation, "libraries",
            Word::class.java
        )
        return result.mappedResults[0]
    }

    override fun getWordIdByLibraryNameTelegramUserIdWordSpelling(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): ObjectId =
        getWordByLibraryNameTelegramUserIdWordSpelling(libraryName, telegramUserId, wordSpelling).id

}
