package systems.ajax.englishstudytelegrambot.repository

import org.bson.types.ObjectId
import org.springframework.dao.DuplicateKeyException
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.exception.LibraryWithTheSameNameForUserAlreadyExistException
import systems.ajax.englishstudytelegrambot.exception.LibraryIsMissingException

interface LibraryRepository {

    fun saveNewLibrary(nameOfNewLibrary: String, telegramUserId: String): Mono<Library>

    fun getAllLibraries(): Flux<Library>

    fun deleteLibrary(libraryId: ObjectId): Mono<Library>

    fun getLibraryByLibraryNameAndTelegramUserId(libraryName: String, telegramUserId: String): Mono<Library>

    fun getLibraryIdByLibraryNameAndTelegramUserId(libraryName: String, telegramUserId: String): Mono<ObjectId>

    fun getAllWordsFromLibrary(libraryId: ObjectId): Flux<Word>
}

@Repository
class LibraryRepositoryImpl(val mongoTemplate: ReactiveMongoTemplate) : LibraryRepository {

    override fun saveNewLibrary(nameOfNewLibrary: String, telegramUserId: String): Mono<Library> =
        try {
            mongoTemplate.insert(Library(name = nameOfNewLibrary, ownerId = telegramUserId))
        } catch (e: DuplicateKeyException) {
            log.error("library with same name exist already {}", e.message)
            throw LibraryWithTheSameNameForUserAlreadyExistException()
        }

    override fun getAllLibraries(): Flux<Library> = mongoTemplate.findAll(Library::class.java)

    override fun deleteLibrary(libraryId: ObjectId): Mono<Library> {
        deleteWordsWhichBelongsToLibrary(libraryId)
        return mongoTemplate.findAndRemove(
            Query.query(Criteria.where("_id").`is`(libraryId)),
            Library::class.java
        )
    }

    override fun getLibraryByLibraryNameAndTelegramUserId(libraryName: String, telegramUserId: String): Mono<Library> =
        mongoTemplate.findOne(
            queryToFindLibraryByNameAndTelegramUserId(telegramUserId, libraryName),
            Library::class.java
        )

    override fun getLibraryIdByLibraryNameAndTelegramUserId(libraryName: String, telegramUserId: String): Mono<ObjectId> =
        (mongoTemplate.findOne(
            queryToFindLibraryByNameAndTelegramUserId(telegramUserId, libraryName).apply {
                this.fields().include("_id")
            },
            Map::class.java,
            "libraries"
        )).map{library -> library.get("_id") as ObjectId}

    private fun queryToFindLibraryByNameAndTelegramUserId(
        telegramUserId: String,
        nameOfLibraryForDeleting: String
    ): Query = Query.query(
        Criteria().andOperator(
            Criteria.where("ownerId").`is`(telegramUserId),
            Criteria.where("name").`is`(nameOfLibraryForDeleting)
        )
    )

    override fun getAllWordsFromLibrary(libraryId: ObjectId): Flux<Word> =
        mongoTemplate.find(
            Query.query(Criteria.where("libraryId").`is`(libraryId)),
            Word::class.java
        )

    private fun deleteWordsWhichBelongsToLibrary(libraryId: ObjectId) {
        mongoTemplate.remove(Query.query(Criteria.where("libraryId").`is`(libraryId)), Word::class.java)
    }

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
