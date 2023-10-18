package systems.ajax.englishstudytelegrambot.repository

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findDistinct
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import systems.ajax.englishstudytelegrambot.annotation.LogMethodsByRequiredAnnotations
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.User
import kotlin.math.log


interface UserRepository {

    fun getAllUsers(): List<User>

    fun getAllLibrariesOfUser(telegramUserId: String): List<Library>
}

@Repository
@LogMethodsByRequiredAnnotations(LogMethodsByRequiredAnnotations::class)
class UserRepositoryImpl(val mongoTemplate: MongoTemplate) : UserRepository {

    @LogMethodsByRequiredAnnotations
    override fun getAllUsers(): List<User> =
        mongoTemplate.find(Query().apply { fields().include("ownerId").exclude("_id") }, User::class.java, "libraries")
            .distinct()

    override fun getAllLibrariesOfUser(telegramUserId: String): List<Library> =
        mongoTemplate.find(
            Query.query(Criteria.where("ownerId").`is`(telegramUserId)),
            Library::class.java
        )
}
