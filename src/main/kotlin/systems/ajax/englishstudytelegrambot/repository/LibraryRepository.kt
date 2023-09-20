package systems.ajax.englishstudytelegrambot.repository

import org.springframework.data.mongodb.repository.MongoRepository
import systems.ajax.englishstudytelegrambot.entity.Library

interface LibraryRepository : MongoRepository<Library, String>
