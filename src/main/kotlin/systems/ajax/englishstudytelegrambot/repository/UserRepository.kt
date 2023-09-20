package systems.ajax.englishstudytelegrambot.repository

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository
import systems.ajax.englishstudytelegrambot.entity.User


interface UserRepository {

    fun getAll(): List<User>

    fun insert(newUser: User): User

    fun getUserByTelegramId(telegramId: String): User?
}

@Repository
class UserRepositoryImpl(val mongoTemplate: MongoTemplate) : UserRepository {

    override fun getAll(): List<User> = mongoTemplate.findAll(User::class.java)

    override fun insert(newUser: User): User = mongoTemplate.insert(newUser)

    override fun getUserByTelegramId(telegramId: String): User? =
        mongoTemplate.findById(telegramId, User::class.java)
}
