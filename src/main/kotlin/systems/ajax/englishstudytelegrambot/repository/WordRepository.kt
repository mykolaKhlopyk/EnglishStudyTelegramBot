package systems.ajax.englishstudytelegrambot.repository

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.exception.LibraryIsMissingException
import systems.ajax.englishstudytelegrambot.exception.WordNotFoundBySpendingException

interface WordRepository {

    fun saveNewWordInLibrary(word: Word, libraryName: String, telegramUserId: String): Library

    fun findById(wordSpelling: String): Word
}

@Repository
class WordRepositoryImpl(
    val mongoTemplate: MongoTemplate
) : WordRepository {

    override fun saveNewWordInLibrary(word: Word, libraryName: String, telegramUserId: String): Library =
        mongoTemplate.findAndModify(
            CommonQuery.queryToFindLibraryByNameAndTelegramUserId(telegramUserId, libraryName),
            Update().push("words", word),
            Library::class.java
        )?: throw LibraryIsMissingException()

    override fun findById(wordSpelling: String): Word =
        mongoTemplate.findById(wordSpelling, Word::class.java)?: throw WordNotFoundBySpendingException()
}
