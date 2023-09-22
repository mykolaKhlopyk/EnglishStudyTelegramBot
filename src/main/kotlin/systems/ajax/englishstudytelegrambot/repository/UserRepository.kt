package systems.ajax.englishstudytelegrambot.repository

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.User


interface UserRepository {

    fun getAllUsers(): List<User>

    fun insert(newUser: User): User

    fun getUserByTelegramId(telegramId: String): User

    fun getAllLibrariesOfUser(telegramIdOfCurrentUser: String): List<Library>
}

@Repository
class UserRepositoryImpl(val mongoTemplate: MongoTemplate) : UserRepository {

    override fun getAllUsers(): List<User> = mongoTemplate.findAll(User::class.java)

    override fun insert(newUser: User): User = mongoTemplate.insert(newUser)

    override fun getUserByTelegramId(telegramId: String): User =
        mongoTemplate.findById(telegramId, User::class.java) ?: insert(User(telegramId))

    override fun getAllLibrariesOfUser(telegramIdOfCurrentUser: String): List<Library> =
        mongoTemplate.find(
            Query.query(Criteria.where("ownerId").`is`(telegramIdOfCurrentUser)),
            Library::class.java
        )
}
