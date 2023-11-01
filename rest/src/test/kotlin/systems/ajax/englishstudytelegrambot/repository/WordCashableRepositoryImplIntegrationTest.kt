package systems.ajax.englishstudytelegrambot.repository

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.kotlin.test.test
import systems.ajax.englishstudytelegrambot.entity.Word
import LibrarySaverInMongoDbForTesting.saveLibraryForTesting
import WordFactory

@SpringBootTest
class WordCashableRepositoryImplIntegrationTest {

    @Autowired
    lateinit var libraryRepository: LibraryRepository

    @Autowired
    lateinit var redisTemplate: ReactiveRedisTemplate<String, Word>

    @Autowired
    lateinit var mongoTemplate: ReactiveMongoTemplate

    @Autowired
    lateinit var wordCashableRepository: WordCashableRepository

    @Test
    fun `should return word from cash when saving is done`() {
        // GIVEN
        val library = libraryRepository.saveLibraryForTesting()
        val word: Word = WordFactory.createWord(library.id)
        val key = buildString {
            append(
                WordCashableRepositoryImpl.KEY_FIND_WORD_BY_ID,
                ":",
                word.id
            )
        }

        // WHEN // THEN
        wordCashableRepository.saveNewWord(word)
            .test()
            .expectNext(word)
            .verifyComplete()

        // AND THEN
        val actualWordFromDb: Word? = mongoTemplate.findOne(
            Query.query(Criteria.where("_id").`is`(word.id)),
            Word::class.java
        ).block()
        Assertions.assertThat(actualWordFromDb).isNotNull
        Assertions.assertThat(actualWordFromDb).isEqualTo(word)

        // AND THEN
        val actualWordFromRedis = redisTemplate.opsForValue().get(key).block()
        Assertions.assertThat(actualWordFromRedis).isNotNull
        Assertions.assertThat(actualWordFromRedis).isEqualTo(word)
    }

    @Test
    fun `should return word by parameters from cash when methods is called second time`() {
        // GIVEN
        val library = libraryRepository.saveLibraryForTesting()
        val word: Word = WordFactory.createWord(library.id)
        val key = buildString {
            append(
                WordCashableRepositoryImpl.KEY_FIND_WORD_BY_PARAMETERS,
                ":",
                library.name,
                ":",
                library.ownerId,
                ":",
                word.spelling
            )
        }

        // WHEN THEN
        wordCashableRepository.saveNewWord(word).block()
        val firstRequestToRedis = redisTemplate.opsForValue().get(key).block()
        wordCashableRepository.getWordByLibraryNameTelegramUserIdWordSpelling(
            library.name,
            library.ownerId,
            word.spelling
        )
            .test()
            .expectNext(word)
            .verifyComplete()
        val secondRequestToRedis = redisTemplate.opsForValue().get(key).block()

        //THEN
        Assertions.assertThat(firstRequestToRedis).isNull()

        Assertions.assertThat(secondRequestToRedis).isNotNull
        Assertions.assertThat(secondRequestToRedis).isEqualTo(word)
    }
}
