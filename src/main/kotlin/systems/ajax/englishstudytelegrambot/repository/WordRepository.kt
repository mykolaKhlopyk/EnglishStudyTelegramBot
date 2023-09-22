package systems.ajax.englishstudytelegrambot.repository

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository
import systems.ajax.englishstudytelegrambot.entity.Word

interface WordRepository {

    fun saveNewWord(word: Word): Word
    fun findById(wordSpelling: String): Word
}

@Repository
class WordRepositoryImpl(val mongoTemplate: MongoTemplate) : WordRepository {

    override fun saveNewWord(word: Word): Word =
        mongoTemplate.save(word)

    override fun findById(wordSpelling: String): Word {
        TODO("Not yet implemented")
    }
}
