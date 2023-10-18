package systems.ajax.englishstudytelegrambot.repository

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import systems.ajax.englishstudytelegrambot.annotation.LogMethodsByRequiredAnnotations
import systems.ajax.englishstudytelegrambot.entity.MongoLibrary
import systems.ajax.englishstudytelegrambot.entity.MongoUser


interface UserRepository {

    fun getAllUsers(): List<MongoUser>

    fun getAllLibrariesOfUser(telegramUserId: String): List<MongoLibrary>
}

@Repository
@LogMethodsByRequiredAnnotations(LogMethodsByRequiredAnnotations::class)
class UserRepositoryImpl(val mongoTemplate: MongoTemplate) : UserRepository {

    @LogMethodsByRequiredAnnotations
    override fun getAllUsers(): List<MongoUser> =
        mongoTemplate.find(Query().apply { fields().include("ownerId").exclude("_id") }, MongoUser::class.java, "libraries")
            .distinct()

    override fun getAllLibrariesOfUser(telegramUserId: String): List<MongoLibrary> =
        mongoTemplate.find(
            Query.query(Criteria.where("ownerId").`is`(telegramUserId)),
            MongoLibrary::class.java
        )
}
