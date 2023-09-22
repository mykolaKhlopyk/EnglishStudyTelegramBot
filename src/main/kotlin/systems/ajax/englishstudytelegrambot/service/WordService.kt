package systems.ajax.englishstudytelegrambot.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.dto.WordDto
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.exception.WordNotFoundBySpendingException
import systems.ajax.englishstudytelegrambot.repository.WordRepository

interface WordService {


    fun getWordByItsSpelling(wordSpelling: String): Word
    suspend fun  saveNewWord(libraryName: String, telegramUserId: String, wordDto: WordDto): Word
}

@Service
class WordServiceImpl(
    val wordRepository: WordRepository,
    val additionalInfoAboutWordService: AdditionalInfoAboutWordService
) : WordService {

    override suspend fun saveNewWord(libraryName: String, telegramUserId: String, wordDto: WordDto): Word {
        val word =
            Word(
                wordDto.spelling,
                wordDto.translate,
                additionalInfoAboutWordService.findAdditionInfoAboutWord(wordDto.spelling)
            )
        log.info("full saving word {}", word)
        return wordRepository.saveNewWord(word)
    }

    override fun getWordByItsSpelling(wordSpelling: String): Word =
        wordRepository.findById(wordSpelling)

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
