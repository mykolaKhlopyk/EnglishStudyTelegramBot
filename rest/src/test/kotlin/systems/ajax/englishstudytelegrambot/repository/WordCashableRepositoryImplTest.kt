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
class WordCashableRepositoryImplTest {

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
        val actualWordFromRedis = redisTemplate.opsForValue().get("WORDS_REDIS_KEY" + word.id.toHexString()).block()
        Assertions.assertThat(actualWordFromRedis).isNotNull
        Assertions.assertThat(actualWordFromRedis).isEqualTo(word)
    }

    @Test
    fun `should when`(){
    // GIVEN

    // WHEN

    // THEN

    }
}
