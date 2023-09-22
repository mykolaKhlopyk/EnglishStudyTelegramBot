package systems.ajax.englishstudytelegrambot.repository

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository
import systems.ajax.englishstudytelegrambot.entity.Library

interface LibraryRepository {

    fun saveNewLibrary(nameOfNewLibrary: String, telegramUserId: String): Library
    fun getAllLibraries(): List<Library>
}

@Repository
class LibraryRepositoryImpl(val mongoTemplate: MongoTemplate) : LibraryRepository {

    override fun saveNewLibrary(nameOfNewLibrary: String, telegramUserId: String): Library =
        mongoTemplate.insert(Library(name = nameOfNewLibrary, ownerId = telegramUserId))

    override fun getAllLibraries(): List<Library> = mongoTemplate.findAll(Library::class.java)
}