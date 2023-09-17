package systems.ajax.englishstudytelegrambot.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.exception.WordNotFoundBySpendingExceptions
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.repository.WordRepository

interface WordService {

    suspend fun save(wordSpelling: String, wordTranslate: String): Word

    fun getWordByItsSpelling(wordSpelling: String): Word
}

@Service
class WordServiceImpl(
    val wordRepository: WordRepository,
    val additionalInfoAboutWordService: AdditionalInfoAboutWordService
) : WordService {

    override suspend fun save(wordSpelling: String, wordTranslate: String): Word {
        val word =
            Word(wordSpelling, wordTranslate, additionalInfoAboutWordService.findAdditionInfoAboutWord(wordSpelling))
        log.info("full saving word {}", word)
        return wordRepository.save(word)
    }

    override fun getWordByItsSpelling(wordSpelling: String): Word =
        wordRepository.findById(wordSpelling).orElseThrow { throw WordNotFoundBySpendingExceptions() }

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
