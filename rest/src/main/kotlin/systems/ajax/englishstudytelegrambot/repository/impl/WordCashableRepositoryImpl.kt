package systems.ajax.englishstudytelegrambot.repository.impl

import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.repository.WordRepository

@Repository
@Suppress("TooManyFunctions" )
class WordCashableRepositoryImpl(
    val redisTemplate: ReactiveRedisTemplate<String, Word>,
    @Qualifier("wordRepositoryImpl") val wordRepository: WordRepository
) : WordRepository {

    override fun saveNewWord(word: Word): Mono<Word> =
        wordRepository.saveNewWord(word)
            .flatMap {
                redisTemplate.opsForValue().set(it.createKeyFindWordById(), word).thenReturn(it)
            }

    override fun updateWordTranslating(wordId: ObjectId, newWordTranslate: String): Mono<Word> =
        wordRepository.updateWordTranslating(wordId, newWordTranslate)
            .flatMap { redisTemplate.opsForValue().set(it.createKeyFindWordById(), it).thenReturn(it) }

    override fun deleteWord(wordId: ObjectId): Mono<Word> =
        wordRepository.deleteWord(wordId)
            .flatMap { redisTemplate.opsForValue().delete(it.createKeyFindWordById()).thenReturn(it) }

    override fun isWordBelongsToLibraryByWordId(wordId: ObjectId, libraryId: ObjectId): Mono<Boolean> =
        getWord(wordId)
            .map { it.libraryId == libraryId }
            .flatMap {
                if (it) {
                    it.toMono()
                } else {
                    Mono.empty()
                }
            }
            .switchIfEmpty {
                wordRepository.isWordBelongsToLibraryByWordId(wordId, libraryId)
            }

    override fun isWordBelongsToLibrary(wordSpelling: String, libraryId: ObjectId): Mono<Boolean> =
        wordRepository.isWordBelongsToLibrary(wordSpelling, libraryId)

    override fun getWord(wordId: ObjectId): Mono<Word> =
        redisTemplate.opsForValue().get(wordId.createKeyFindWordById())
            .doOnNext {
                println(it)
            }
            .switchIfEmpty {
                wordRepository.getWord(wordId).flatMap {
                    redisTemplate.opsForValue().set(it.createKeyFindWordById(), it).thenReturn(it)
                }
            }

    override fun getWordIdBySpellingAndLibraryId(wordSpelling: String, libraryId: ObjectId): Mono<ObjectId> =
        wordRepository.getWordIdBySpellingAndLibraryId(wordSpelling, libraryId)

    override fun getAllWords(): Flux<Word> = wordRepository.getAllWords()

    override fun getWordByLibraryNameTelegramUserIdWordSpelling(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
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
        wordSpelling: String
    ): Mono<ObjectId> =
        getWordByLibraryNameTelegramUserIdWordSpelling(libraryName, telegramUserId, wordSpelling).map(Word::id)

    override fun getAllWordsFromLibrary(libraryId: ObjectId): Flux<Word> =
        wordRepository.getAllWordsFromLibrary(libraryId)

    private fun Word.createKeyFindWordById(): String =
        id.createKeyFindWordById()

    private fun ObjectId.createKeyFindWordById(): String =
        buildString {
            append(KEY_FIND_WORD_BY_ID, ":", toHexString())
        }

    private fun createKeyFindWordByParameters(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ) = buildString {
        append(KEY_FIND_WORD_BY_PARAMETERS, ":", libraryName, ":", telegramUserId, ":", wordSpelling)
    }

    companion object {
        const val KEY_FIND_WORD_BY_ID = "wordsRedisKeyFindWordById"
        const val KEY_FIND_WORD_BY_PARAMETERS = "wordsRedisKeyFindByTelegramUserIdLibraryNameWordSpelling"
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
