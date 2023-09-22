package systems.ajax.englishstudytelegrambot.repository

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.exception.LibraryIsMissingException

interface LibraryRepository {

    fun saveNewLibrary(nameOfNewLibrary: String, telegramUserId: String): Library
    fun getAllLibraries(): List<Library>
    fun deleteLibrary(nameOfLibraryForDeleting: String, telegramUserId: String): Library
}

@Repository
class LibraryRepositoryImpl(val mongoTemplate: MongoTemplate) : LibraryRepository {

    override fun saveNewLibrary(nameOfNewLibrary: String, telegramUserId: String): Library =
        mongoTemplate.insert(Library(name = nameOfNewLibrary, ownerId = telegramUserId))

    override fun getAllLibraries(): List<Library> = mongoTemplate.findAll(Library::class.java)

    override fun deleteLibrary(nameOfLibraryForDeleting: String, telegramUserId: String): Library =
        mongoTemplate.findAndRemove(
            Query.query(
                Criteria().andOperator(
                    Criteria.where("ownerId").`is`(telegramUserId),
                    Criteria.where("name").`is`(nameOfLibraryForDeleting)
                )
            ),
            Library::class.java
        ) ?: throw LibraryIsMissingException()
}