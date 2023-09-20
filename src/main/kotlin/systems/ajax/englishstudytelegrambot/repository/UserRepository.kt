package systems.ajax.englishstudytelegrambot.repository

import org.springframework.data.mongodb.repository.MongoRepository
import systems.ajax.englishstudytelegrambot.entity.User

interface UserRepository:MongoRepository<User, String>