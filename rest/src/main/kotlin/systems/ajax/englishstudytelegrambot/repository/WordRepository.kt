package systems.ajax.englishstudytelegrambot.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.aggregation.LookupOperation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.englishstudytelegrambot.entity.Word


interface WordRepository {

    fun saveNewWord(word: Word): Mono<Word>

    fun updateWordTranslating(wordId: ObjectId, newWordTranslate: String): Mono<Word>

    fun deleteWord(wordId: ObjectId): Mono<Word>

    fun isWordBelongsToLibraryByWordId(wordId: ObjectId, libraryId: ObjectId): Mono<Boolean>

    fun isWordBelongsToLibrary(wordSpelling: String, libraryId: ObjectId): Mono<Boolean>

    fun getWord(wordId: ObjectId): Mono<Word>

    fun getWordIdBySpellingAndLibraryId(wordSpelling: String, libraryId: ObjectId): Mono<ObjectId>

    fun getAllWords(): Flux<Word>

    fun getWordByLibraryNameTelegramUserIdWordSpelling(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): Mono<Word>

    fun getWordIdByLibraryNameTelegramUserIdWordSpelling(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): Mono<ObjectId>
}

@Repository
class WordRepositoryImpl(
    val mongoTemplate: ReactiveMongoTemplate
) : WordRepository {

    override fun saveNewWord(word: Word): Mono<Word> =
        mongoTemplate.save(word)

    override fun updateWordTranslating(wordId: ObjectId, newWordTranslate: String): Mono<Word> =
        mongoTemplate.findAndModify<Word>(
            Query.query(Criteria.where("_id").`is`(wordId)),
            Update.update("translate", newWordTranslate),
            FindAndModifyOptions().returnNew(true)
        )

    override fun deleteWord(wordId: ObjectId): Mono<Word> =
        mongoTemplate.findAndRemove<Word>(
            Query.query(Criteria.where("_id").`is`(wordId))
        )

    override fun isWordBelongsToLibraryByWordId(wordId: ObjectId, libraryId: ObjectId): Mono<Boolean> =
        mongoTemplate.exists<Word>(
            Query.query(Criteria.where("libraryId").`is`(libraryId).and("id").`is`(wordId))
        )

    override fun isWordBelongsToLibrary(wordSpelling: String, libraryId: ObjectId): Mono<Boolean> =
        mongoTemplate.exists<Word>(
            Query.query(Criteria.where("libraryId").`is`(libraryId).and("spelling").`is`(wordSpelling))
        )

    override fun getWord(wordId: ObjectId): Mono<Word> =
        mongoTemplate.findById<Word>(wordId)

    override fun getAllWords(): Flux<Word> =
        mongoTemplate.findAll<Word>()

    override fun getWordIdBySpellingAndLibraryId(wordSpelling: String, libraryId: ObjectId): Mono<ObjectId> =
        mongoTemplate.findOne<Word>(
            Query.query(Criteria.where("spelling").`is`(wordSpelling).and("libraryId").`is`(libraryId))
        ).map(Word::id)

    override fun getWordByLibraryNameTelegramUserIdWordSpelling(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): Mono<Word> {
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
        val result = mongoTemplate.aggregate<Word>(
            aggregation, "libraries"
        )
        return result.next()
    }

    override fun getWordIdByLibraryNameTelegramUserIdWordSpelling(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): Mono<ObjectId> =
        getWordByLibraryNameTelegramUserIdWordSpelling(libraryName, telegramUserId, wordSpelling).map(Word::id)
}
