package systems.ajax.englishstudytelegrambot.repository

import org.springframework.data.mongodb.repository.MongoRepository
import systems.ajax.englishstudytelegrambot.entity.Word

interface WordRepository : MongoRepository<Word, String>
