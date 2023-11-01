package systems.ajax.englishstudytelegrambot.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.findAndRemove
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.remove
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.Word

interface LibraryRepository {

    fun saveNewLibrary(nameOfNewLibrary: String, telegramUserId: String): Mono<Library>

    fun getAllLibraries(): Flux<Library>

    fun deleteLibrary(libraryId: ObjectId): Mono<Library>

    fun getLibraryByLibraryNameAndTelegramUserId(libraryName: String, telegramUserId: String): Mono<Library>

    fun getLibraryIdByLibraryNameAndTelegramUserId(libraryName: String, telegramUserId: String): Mono<ObjectId>

    fun getLibraryById(id: ObjectId): Mono<Library>
}

@Repository
class LibraryRepositoryImpl(val mongoTemplate: ReactiveMongoTemplate) : LibraryRepository {

    override fun saveNewLibrary(nameOfNewLibrary: String, telegramUserId: String): Mono<Library> =
        mongoTemplate.insert(Library(name = nameOfNewLibrary, ownerId = telegramUserId))

    override fun getAllLibraries(): Flux<Library> = mongoTemplate.findAll<Library>()

    override fun deleteLibrary(libraryId: ObjectId): Mono<Library> =
        deleteAllWordsFromLibrary(libraryId).then(
            mongoTemplate.findAndRemove<Library>(
                Query.query(Criteria.where("_id").`is`(libraryId))
            )
        )

    override fun getLibraryByLibraryNameAndTelegramUserId(libraryName: String, telegramUserId: String): Mono<Library> =
        mongoTemplate.findOne<Library>(
            queryToFindLibraryByNameAndTelegramUserId(telegramUserId, libraryName)
        )

    override fun getLibraryIdByLibraryNameAndTelegramUserId(
        libraryName: String,
        telegramUserId: String
    ): Mono<ObjectId> =
        mongoTemplate
            .findOne(
                queryToFindLibraryByNameAndTelegramUserId(telegramUserId, libraryName).apply {
                    this.fields().include("_id")
                },
                Map::class.java,
                "libraries"
            )
            .map { library -> library.get("_id") as ObjectId }

    private fun queryToFindLibraryByNameAndTelegramUserId(
        telegramUserId: String,
        nameOfLibraryForDeleting: String
    ): Query =
        Query.query(
            Criteria().andOperator(
                Criteria.where("ownerId").`is`(telegramUserId),
                Criteria.where("name").`is`(nameOfLibraryForDeleting)
            )
        )

    override fun getLibraryById(id: ObjectId): Mono<Library> =
        mongoTemplate.findById<Library>(id)

    private fun deleteAllWordsFromLibrary(libraryId: ObjectId) =
        mongoTemplate.remove<Word>(Query.query(Criteria.where("libraryId").`is`(libraryId)))
}
