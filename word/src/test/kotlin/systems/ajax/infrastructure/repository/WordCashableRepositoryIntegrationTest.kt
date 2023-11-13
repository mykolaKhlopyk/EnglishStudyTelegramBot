package systems.ajax.infrastructure.repository

import LibrarySaverInMongoDbForTesting.saveLibraryForTesting
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.kotlin.test.test
import systems.ajax.domain.model.Word
import systems.ajax.infrastructure.mongo.entity.MongoWord
import systems.ajax.infrastructure.mongo.mapper.toModel
import systems.ajax.infrastructure.mongo.repository.LibraryRepository
import systems.ajax.infrastructure.redis.repository.WordCashableRepository

@SpringBootTest
class WordCashableRepositoryIntegrationTest {

    @Autowired
    lateinit var libraryRepository: LibraryRepository

    @Autowired
    lateinit var redisTemplate: ReactiveRedisTemplate<String, Word>

    @Autowired
    lateinit var mongoTemplate: ReactiveMongoTemplate

    @Autowired
    @Qualifier("wordCashableRepository")
    lateinit var wordRepository: WordCashableRepository

    @Test
    fun `should return word from cash when saving is done`() {
        // GIVEN
        val library = libraryRepository.saveLibraryForTesting()
        val word = WordFactory.createWord(library.id)
        val key = buildString {
            append(
                WordCashableRepository.KEY_FIND_WORD_BY_ID,
                ":",
                word.id
            )
        }

        // WHEN // THEN
        wordRepository.saveNewWord(word)
            .test()
            .expectNext(word)
            .verifyComplete()

        // AND THEN
        val actualWordFromDb: MongoWord? = mongoTemplate.findOne<MongoWord>(
            Query.query(Criteria.where("_id").`is`(ObjectId(word.id)))
        ).block()
        Assertions.assertNotNull(actualWordFromDb)
        Assertions.assertEquals(word, actualWordFromDb!!.toModel())

        // AND THEN
        val actualWordFromRedis = redisTemplate.opsForValue().get(key).block()
        Assertions.assertNotNull(actualWordFromRedis)
        Assertions.assertEquals(actualWordFromRedis, word)
    }

    @Test
    fun `should return word by parameters from cash when methods is called second time`() {
        // GIVEN
        val library = libraryRepository.saveLibraryForTesting()
        val word = WordFactory.createWord(library.id)
        val key = buildString {
            append(
                WordCashableRepository.KEY_FIND_WORD_BY_PARAMETERS,
                ":",
                library.name,
                ":",
                library.ownerId,
                ":",
                word.spelling
            )
        }

        // WHEN THEN
        wordRepository.saveNewWord(word).block()
        val firstRequestToRedis = redisTemplate.opsForValue().get(key).block()
        wordRepository.getWordByLibraryNameTelegramUserIdWordSpelling(
            library.name,
            library.ownerId,
            word.spelling
        )
            .test()
            .expectNext(word)
            .verifyComplete()
        val secondRequestToRedis = redisTemplate.opsForValue().get(key).block()

        //THEN
        Assertions.assertNull(firstRequestToRedis)

        Assertions.assertNotNull(secondRequestToRedis)
        Assertions.assertEquals(word, secondRequestToRedis)
    }
}