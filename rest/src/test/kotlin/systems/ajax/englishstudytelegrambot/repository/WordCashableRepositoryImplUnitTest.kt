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
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.repository.impl.WordCashableRepositoryImpl

@ExtendWith(MockKExtension::class)
class WordCashableRepositoryImplUnitTest {

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
    fun `should execute method from wordRepository method when redis doesn't contains word`() {
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
        verify(exactly = 1) { redisOpsMock.set(any(), any()) }
    }

    @Test
    fun `should not execute method for searching word from wordRepository  when redis contains word`() {
        // GIVEN
        val word = mockk<Word> {
            every { id } returns ObjectId()
        }

        val redisOpsMock = mockk<ReactiveValueOperations<String, Word>> {
            every { this@mockk.get(any()) } returns word.toMono()
        }

        every { redisTemplate.opsForValue() } returns redisOpsMock

        // WHEN
        wordCashableRepository.getWord(word.id)
            .test()
            .expectNext(word)
            .verifyComplete()

        // THEN
        verify(exactly = 1) { redisOpsMock.get(any()) }
        verify(exactly = 0) { wordRepository.getWord(any()) }
    }

    @Test
    fun `should execute method for searching word from wordRepository when redis doesn't contains word`() {
        // GIVEN
        val word = mockk<Word> {
            every { id } returns ObjectId()
        }
        every { wordRepository.getWordByLibraryNameTelegramUserIdWordSpelling(any(), any(), any()) } returns Mono.just(
            word
        )

        val redisOpsMock = mockk<ReactiveValueOperations<String, Word>>()

        every { redisOpsMock.get(any()) } returns Mono.empty()
        every { redisOpsMock.set(any(), word) } returns Mono.just(true)

        every { redisTemplate.opsForValue() } returns redisOpsMock

        // WHEN
        wordCashableRepository.getWordByLibraryNameTelegramUserIdWordSpelling("", "", "")
            .test()
            .expectNext(word)
            .verifyComplete()

        // THEN
        verify(exactly = 1) { redisOpsMock.get(any()) }
        verify(exactly = 1) { wordRepository.getWordByLibraryNameTelegramUserIdWordSpelling(any(), any(), any()) }
        verify(exactly = 1) { redisOpsMock.set(any(), any()) }
    }

    @Test
    fun `should not execute method from wordRepository method when redis doesn't contains word`() {
        // GIVEN
        val word = mockk<Word> {
            every { id } returns ObjectId()
        }

        val redisOpsMock = mockk<ReactiveValueOperations<String, Word>>()

        every { redisOpsMock.get(any()) } returns Mono.just(word)

        every { redisTemplate.opsForValue() } returns redisOpsMock

        // WHEN
        wordCashableRepository.getWordByLibraryNameTelegramUserIdWordSpelling("", "", "")
            .test()
            .expectNext(word)
            .verifyComplete()

        // THEN
        verify(exactly = 1) { redisOpsMock.get(any()) }
        verify(exactly = 0) { wordRepository.getWordByLibraryNameTelegramUserIdWordSpelling(any(), any(), any()) }
    }
}
