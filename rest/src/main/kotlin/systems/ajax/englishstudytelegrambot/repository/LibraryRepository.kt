package systems.ajax.englishstudytelegrambot.repository

import org.bson.types.ObjectId
import org.springframework.dao.DuplicateKeyException
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import systems.ajax.englishstudytelegrambot.entity.MongoLibrary
import systems.ajax.englishstudytelegrambot.entity.MongoWord
import systems.ajax.englishstudytelegrambot.exception.LibraryWithTheSameNameForUserAlreadyExistException
import systems.ajax.englishstudytelegrambot.exception.LibraryIsMissingException

interface LibraryRepository {

    fun saveNewLibrary(nameOfNewLibrary: String, telegramUserId: String): MongoLibrary

    fun getAllLibraries(): List<MongoLibrary>

    fun deleteLibrary(libraryId: ObjectId): MongoLibrary

    fun getLibraryByLibraryNameAndTelegramUserId(libraryName: String, telegramUserId: String): MongoLibrary

    fun getLibraryIdByLibraryNameAndTelegramUserId(libraryName: String, telegramUserId: String): ObjectId

    fun getAllWordsFromLibrary(libraryId: ObjectId): List<MongoWord>
}

@Repository
class LibraryRepositoryImpl(val mongoTemplate: MongoTemplate) : LibraryRepository {

    override fun saveNewLibrary(nameOfNewLibrary: String, telegramUserId: String): MongoLibrary =
        try {
            mongoTemplate.insert(MongoLibrary(name = nameOfNewLibrary, ownerId = telegramUserId))
        } catch (e: DuplicateKeyException) {
            log.error("library with same name exist already {}", e.message)
            throw LibraryWithTheSameNameForUserAlreadyExistException()
        }

    override fun getAllLibraries(): List<MongoLibrary> = mongoTemplate.findAll(MongoLibrary::class.java)

    override fun deleteLibrary(libraryId: ObjectId): MongoLibrary {
        deleteWordsWhichBelongsToLibrary(libraryId)
        return mongoTemplate.findAndRemove(
            Query.query(Criteria.where("_id").`is`(libraryId)),
            MongoLibrary::class.java
        ) ?: throw LibraryIsMissingException()
    }

    override fun getLibraryByLibraryNameAndTelegramUserId(libraryName: String, telegramUserId: String): MongoLibrary =
        mongoTemplate.findOne(
            queryToFindLibraryByNameAndTelegramUserId(telegramUserId, libraryName),
            MongoLibrary::class.java
        ) ?: throw LibraryIsMissingException()

    override fun getLibraryIdByLibraryNameAndTelegramUserId(libraryName: String, telegramUserId: String): ObjectId =
        (mongoTemplate.findOne(
            queryToFindLibraryByNameAndTelegramUserId(telegramUserId, libraryName).apply {
                this.fields().include("_id")
            },
            Map::class.java,
            "libraries"
        )?.get("_id") as? ObjectId) ?: throw LibraryIsMissingException()

    private fun queryToFindLibraryByNameAndTelegramUserId(
        telegramUserId: String,
        nameOfLibraryForDeleting: String
    ): Query = Query.query(
        Criteria().andOperator(
            Criteria.where("ownerId").`is`(telegramUserId),
            Criteria.where("name").`is`(nameOfLibraryForDeleting)
        )
    )

    override fun getAllWordsFromLibrary(libraryId: ObjectId): List<MongoWord> =
        mongoTemplate.find(
            Query.query(Criteria.where("libraryId").`is`(libraryId)),
            MongoWord::class.java
        )

    private fun deleteWordsWhichBelongsToLibrary(libraryId: ObjectId) {
        mongoTemplate.remove(Query.query(Criteria.where("libraryId").`is`(libraryId)), MongoWord::class.java)
    }

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
