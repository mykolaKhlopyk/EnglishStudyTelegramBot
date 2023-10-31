package systems.ajax.englishstudytelegrambot.repository

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import systems.ajax.englishstudytelegrambot.entity.Word

@ExtendWith(MockKExtension::class)
class WordCashableRepositoryImplUnitTest {

    private val WORD_KEY = "WORDS_REDIS_KEY"

    @MockK
    lateinit var redisTemplate: ReactiveRedisTemplate<String, Word>

    @MockK
    lateinit var wordRepository: WordRepository

    @InjectMockKs
    lateinit var wordCashableRepository: WordCashableRepositoryImpl

    @Test
    fun `should execute two methods when saving`() {
        // GIVEN
        val word = mockk<Word> {
            every { id } returns ObjectId()
        }
        every { wordRepository.saveNewWord(any()) } returns Mono.just(word)

        val redisOpsMock = mockk<ReactiveValueOperations<String, Word>> {
            every { set(any(), word) } returns Mono.just(true)
        }
        every { redisTemplate.opsForValue() } returns redisOpsMock

        // WHEN
        wordCashableRepository.saveNewWord(word)
            .test()
            .expectNext(word)
            .verifyComplete()

        // THEN
        verify(exactly = 1) { wordRepository.saveNewWord(any()) }
        verify(exactly = 1) { redisOpsMock.set(any(), word) }
    }

    @Test
    fun `should execute two methods when deleting`() {
        // GIVEN
        val word = mockk<Word> {
            every { id } returns ObjectId()
        }
        every { wordRepository.deleteWord(any()) } returns Mono.just(word)

        val redisOpsMock = mockk<ReactiveValueOperations<String, Word>> {
            every { delete(any()) } returns Mono.just(true)
        }
        every { redisTemplate.opsForValue() } returns redisOpsMock

        // WHEN
        wordCashableRepository.deleteWord(word.id)
            .test()
            .expectNext(word)
            .verifyComplete()

        // THEN
        verify(exactly = 1) { wordRepository.deleteWord(any()) }
        verify(exactly = 1) { redisOpsMock.delete(any()) }
    }

    @Test
    fun `should execute getWord from wordRepository method once when redisTemplate doesn't contains word`() {
        // GIVEN
        val word = mockk<Word> {
            every { id } returns ObjectId()
        }
        every { wordRepository.getWord(any()) } returns Mono.just(word)

        val redisOpsMock = mockk<ReactiveValueOperations<String, Word>>()

        every { redisOpsMock.get(any()) } returns Mono.empty()
        every { redisOpsMock.set(any(), word) } returns Mono.just(true)

        every { redisTemplate.opsForValue() } returns redisOpsMock

        // WHEN
        wordCashableRepository.getWord(word.id)
            .test()
            .expectNext(word)
            .verifyComplete()

        // THEN
        verify(exactly = 1) { redisOpsMock.get(any()) }
        verify(exactly = 1) { wordRepository.getWord(any()) }
        verify(exactly = 1) { redisOpsMock.set(any(), any())}
    }

    @Test
    fun `should not execute getWord from wordRepository method once when redisTemplate contains word`() {
        // GIVEN
        val word = mockk<Word> {
            every { id } returns ObjectId()
        }
        every { wordRepository.getWord(any()) } returns Mono.empty()

        val redisOpsMock = mockk<ReactiveValueOperations<String, Word>>()

        every { redisOpsMock.get(any()) } returns word.toMono()
        every { redisOpsMock.set(any(), any()) } returns Mono.empty()

        every { redisTemplate.opsForValue() } returns redisOpsMock

        // WHEN
        wordCashableRepository.getWord(word.id)
            .test()
            .expectNext(word)
            .verifyComplete()

        // THEN
        verify(exactly = 1) { redisOpsMock.get(any()) }
        verify(exactly = 0) { wordRepository.getWord(any()) }
        verify(exactly = 0) { redisOpsMock.set(any(), any())}
    }

    private fun Word.createWordKey(): String =
        WORD_KEY + id
}
