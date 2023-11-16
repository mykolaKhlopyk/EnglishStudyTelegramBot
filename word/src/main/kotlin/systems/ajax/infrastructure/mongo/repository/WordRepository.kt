package systems.ajax.infrastructure.mongo.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.aggregation.LookupOperation
import org.springframework.data.mongodb.core.exists
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.findAndModify
import org.springframework.data.mongodb.core.findAndRemove
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.remove
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.application.ports.output.WordsDeletingFromLibraryRepositoryOutPort
import systems.ajax.application.ports.output.WordRepositoryOutPort
import systems.ajax.domain.model.Word
import systems.ajax.infrastructure.mongo.entity.MongoWord
import systems.ajax.infrastructure.mongo.mapper.toModel
import systems.ajax.infrastructure.mongo.mapper.toMongoEntity

@Repository
@Suppress("TooManyFunctions")
class WordRepository(
    val mongoTemplate: ReactiveMongoTemplate,
) : WordRepositoryOutPort, WordsDeletingFromLibraryRepositoryOutPort {

    override fun saveNewWord(word: Word): Mono<Word> =
        mongoTemplate.save(word.toMongoEntity()).map(MongoWord::toModel)

    override fun updateWordTranslating(wordId: String, newWordTranslate: String): Mono<Word> =
        mongoTemplate.findAndModify<MongoWord>(
            Query.query(Criteria.where("_id").`is`(ObjectId(wordId))),
            Update.update("translate", newWordTranslate),
            FindAndModifyOptions().returnNew(true)
        ).map(MongoWord::toModel)

    override fun deleteWord(wordId: String): Mono<Word> =
        mongoTemplate.findAndRemove<MongoWord>(
            Query.query(Criteria.where("_id").`is`(ObjectId(wordId)))
        ).map(MongoWord::toModel)

    override fun isWordBelongsToLibraryByWordId(wordId: String, libraryId: String): Mono<Boolean> =
        mongoTemplate.exists<MongoWord>(
            Query.query(Criteria.where("libraryId").`is`(ObjectId(libraryId)).and("id").`is`(ObjectId(wordId)))
        )

    override fun isWordBelongsToLibrary(wordSpelling: String, libraryId: String): Mono<Boolean> =
        mongoTemplate.exists<MongoWord>(
            Query.query(Criteria.where("libraryId").`is`(ObjectId(libraryId)).and("spelling").`is`(wordSpelling))
        )

    override fun getWord(wordId: String): Mono<Word> =
        mongoTemplate.findById<MongoWord>(ObjectId(wordId)).map(MongoWord::toModel)

    override fun getAllWords(): Flux<Word> =
        mongoTemplate.findAll<MongoWord>().map(MongoWord::toModel)

    override fun getWordIdBySpellingAndLibraryId(wordSpelling: String, libraryId: String): Mono<String> =
        mongoTemplate.findOne<MongoWord>(
            Query.query(Criteria.where("spelling").`is`(wordSpelling).and("libraryId").`is`(ObjectId(libraryId)))
        ).map(MongoWord::id).map(ObjectId::toHexString)

    override fun getWordByLibraryNameTelegramUserIdWordSpelling(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String,
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
        val result = mongoTemplate.aggregate<MongoWord>(
            aggregation, "libraries"
        )
        return result.next().map(MongoWord::toModel)
    }

    override fun getWordIdByLibraryNameTelegramUserIdWordSpelling(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String,
    ): Mono<String> =
        getWordByLibraryNameTelegramUserIdWordSpelling(
            libraryName,
            telegramUserId,
            wordSpelling
        ).map(Word::id)

    override fun getAllWordsFromLibrary(libraryId: String): Flux<Word> =
        mongoTemplate.find<MongoWord>(
            Query.query(Criteria.where("libraryId").`is`(ObjectId(libraryId)))
        ).map(MongoWord::toModel)

    override fun deleteAllWordsFromLibrary(libraryId: String): Mono<Unit> =
        mongoTemplate.remove<MongoWord>(
            Query.query(Criteria.where("libraryId").`is`(ObjectId(libraryId)))
        ).then(Unit.toMono())

    override fun getAllWordsWithSpelling(spelling: String): Flux<Word> =
        mongoTemplate.find<MongoWord>(
            Query.query(Criteria.where("spelling").`is`(spelling))
        ).map(MongoWord::toModel)
}
