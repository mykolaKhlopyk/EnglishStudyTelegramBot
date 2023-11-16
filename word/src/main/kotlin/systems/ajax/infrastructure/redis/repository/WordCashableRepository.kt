package systems.ajax.infrastructure.redis.repository

import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import systems.ajax.application.ports.output.WordsDeletingFromLibraryRepositoryOutPort
import systems.ajax.application.ports.output.WordRepositoryOutPort
import systems.ajax.domain.model.Word

@Repository
@Suppress("TooManyFunctions")
class WordCashableRepository(
    val redisTemplate: ReactiveRedisTemplate<String, Word>,
    @Qualifier("wordRepository") val wordRepository: WordRepositoryOutPort,
    @Qualifier("wordRepository") val wordsDeletingFromLibraryRepositoryOutPort: WordsDeletingFromLibraryRepositoryOutPort,
) : WordRepositoryOutPort, WordsDeletingFromLibraryRepositoryOutPort {

    override fun saveNewWord(word: Word): Mono<Word> =
        wordRepository.saveNewWord(word)
            .flatMap {
                redisTemplate.opsForValue().set(it.createKeyFindWordById(), word).thenReturn(it)
            }

    override fun updateWordTranslating(wordId: String, newWordTranslate: String): Mono<Word> =
        wordRepository.updateWordTranslating(wordId, newWordTranslate)
            .flatMap { redisTemplate.opsForValue().set(it.createKeyFindWordById(), it).thenReturn(it) }

    override fun deleteWord(wordId: String): Mono<Word> =
        wordRepository.deleteWord(wordId)
            .flatMap { redisTemplate.opsForValue().delete(it.createKeyFindWordById()).thenReturn(it) }

    override fun isWordBelongsToLibraryByWordId(wordId: String, libraryId: String): Mono<Boolean> =
        getWord(wordId)
            .map { it.libraryId == libraryId }
            .filter { it }
            .switchIfEmpty {
                wordRepository.isWordBelongsToLibraryByWordId(wordId, libraryId)
            }

    override fun isWordBelongsToLibrary(wordSpelling: String, libraryId: String): Mono<Boolean> =
        wordRepository.isWordBelongsToLibrary(wordSpelling, libraryId)

    override fun getWord(wordId: String): Mono<Word> =
        redisTemplate.opsForValue().get(wordId.createKeyFindWordById())
            .switchIfEmpty {
                wordRepository.getWord(wordId).flatMap {
                    redisTemplate.opsForValue().set(it.createKeyFindWordById(), it).thenReturn(it)
                }
            }

    override fun getWordIdBySpellingAndLibraryId(wordSpelling: String, libraryId: String): Mono<String> =
        wordRepository.getWordIdBySpellingAndLibraryId(wordSpelling, libraryId)

    override fun getAllWords(): Flux<Word> = wordRepository.getAllWords()

    override fun getWordByLibraryNameTelegramUserIdWordSpelling(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String,
    ): Mono<Word> {
        val keyFindWordByParameters = createKeyFindWordByParameters(libraryName, telegramUserId, wordSpelling)
        return redisTemplate.opsForValue()
            .get(keyFindWordByParameters)
            .switchIfEmpty {
                wordRepository.getWordByLibraryNameTelegramUserIdWordSpelling(libraryName, telegramUserId, wordSpelling)
                    .flatMap {
                        redisTemplate.opsForValue()
                            .set(keyFindWordByParameters, it)
                            .thenReturn(it)
                    }
            }
    }

    override fun getWordIdByLibraryNameTelegramUserIdWordSpelling(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String,
    ): Mono<String> =
        getWordByLibraryNameTelegramUserIdWordSpelling(libraryName, telegramUserId, wordSpelling)
            .map(Word::id)

    override fun getAllWordsFromLibrary(libraryId: String): Flux<Word> =
        wordRepository.getAllWordsFromLibrary(libraryId)

    override fun getAllWordsWithSpelling(spelling: String): Flux<Word> =
        wordRepository.getAllWordsWithSpelling(spelling)

    override fun deleteAllWordsFromLibrary(libraryId: String): Mono<Unit> =
        wordsDeletingFromLibraryRepositoryOutPort.deleteAllWordsFromLibrary(libraryId)

    private fun Word.createKeyFindWordById(): String =
        id.createKeyFindWordById()

    private fun ObjectId.createKeyFindWordById(): String =
        buildString {
            append(KEY_FIND_WORD_BY_ID, ":", toHexString())
        }

    private fun String.createKeyFindWordById(): String =
        buildString {
            append(KEY_FIND_WORD_BY_ID, ":", this@createKeyFindWordById)
        }

    private fun createKeyFindWordByParameters(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String,
    ) = buildString {
        append(KEY_FIND_WORD_BY_PARAMETERS, ":", libraryName, ":", telegramUserId, ":", wordSpelling)
    }

    companion object {
        const val KEY_FIND_WORD_BY_ID = "wordsRedisKeyFindWordById"
        const val KEY_FIND_WORD_BY_PARAMETERS = "wordsRedisKeyFindByTelegramUserIdLibraryNameWordSpelling"
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
