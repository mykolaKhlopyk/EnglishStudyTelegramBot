package systems.ajax.infrastructure.mongo.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findAndRemove
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.application.ports.output.LibraryRepositoryOutPort
import systems.ajax.domain.model.Library
import systems.ajax.infrastructure.mapper.toModel
import systems.ajax.infrastructure.mongo.entity.MongoLibrary

@Repository
class LibraryRepository(private val mongoTemplate: ReactiveMongoTemplate) : LibraryRepositoryOutPort {

    override fun saveNewLibrary(nameOfNewLibrary: String, telegramUserId: String): Mono<Library> =
        mongoTemplate.insert(MongoLibrary(name = nameOfNewLibrary, ownerId = telegramUserId))
            .map(MongoLibrary::toModel)

    override fun deleteLibrary(libraryId: String): Mono<Library> =
        mongoTemplate.findAndRemove<MongoLibrary>(
            Query.query(Criteria.where("_id").`is`(ObjectId(libraryId)))
        ).map(MongoLibrary::toModel)

    override fun getLibraryByLibraryNameAndTelegramUserId(libraryName: String, telegramUserId: String): Mono<Library> =
        mongoTemplate.findOne<MongoLibrary>(
            queryToFindLibraryByNameAndTelegramUserId(telegramUserId, libraryName)
        ).map(MongoLibrary::toModel)

    override fun getLibraryIdByLibraryNameAndTelegramUserId(
        libraryName: String,
        telegramUserId: String,
    ): Mono<String> =
        mongoTemplate
            .findOne(
                queryToFindLibraryByNameAndTelegramUserId(telegramUserId, libraryName).apply {
                    this.fields().include("_id")
                },
                Map::class.java,
                "libraries"
            )
            .map { library -> library.get("_id") as ObjectId }
            .map(ObjectId::toHexString)

    private fun queryToFindLibraryByNameAndTelegramUserId(
        telegramUserId: String,
        nameOfLibraryForDeleting: String,
    ): Query =
        Query.query(
            Criteria().andOperator(
                Criteria.where("ownerId").`is`(telegramUserId),
                Criteria.where("name").`is`(nameOfLibraryForDeleting)
            )
        )

    override fun getLibraryById(id: String): Mono<Library> =
        mongoTemplate.findById<MongoLibrary>(ObjectId(id)).map(MongoLibrary::toModel)

    override fun getAllLibrariesOfUser(telegramUserId: String): Flux<Library> =
        mongoTemplate.find<MongoLibrary>(Query.query(Criteria.where("ownerId").`is`(telegramUserId))).map(MongoLibrary::toModel)
}
