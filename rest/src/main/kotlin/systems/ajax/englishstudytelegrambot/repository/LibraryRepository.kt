package systems.ajax.englishstudytelegrambot.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.Word
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

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
        mongoTemplate.insert(Library(name = nameOfNewLibrary, ownerId = telegramUserId))

    override fun getAllLibraries(): Flux<Library> = mongoTemplate.findAll<Library>()

    override fun deleteLibrary(libraryId: ObjectId): Mono<Library> =
        Mono
            .zip(
                deleteAllWordsFromLibrary(libraryId),
                mongoTemplate.findAndRemove(
                    Query.query(Criteria.where("_id").`is`(libraryId)),
                    Library::class.java
                )
            )
            .map { (_, result) -> result }

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

    override fun getAllWordsFromLibrary(libraryId: ObjectId): Flux<Word> =
        mongoTemplate.find<Word>(
            Query.query(Criteria.where("libraryId").`is`(libraryId))
        )

    private fun deleteAllWordsFromLibrary(libraryId: ObjectId) =
        mongoTemplate.remove<Word>(Query.query(Criteria.where("libraryId").`is`(libraryId)))
}
