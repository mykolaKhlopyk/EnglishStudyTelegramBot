package systems.ajax.englishstudytelegrambot.repository

import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.exception.LibraryIsMissingException

interface WordRepository {

    fun saveNewWordInLibrary(word: Word, library: Library): Library
}

@Repository
class WordRepositoryImpl(
    val mongoTemplate: MongoTemplate
) : WordRepository {

    override fun saveNewWordInLibrary(word: Word, library: Library): Library =
        mongoTemplate.findAndModify(
            Query.query(Criteria.where("_id").`is`(library.id)),
            Update().push("words", word),
            FindAndModifyOptions().returnNew(true),
            Library::class.java
        ) ?: throw LibraryIsMissingException()
}
