package systems.ajax.englishstudytelegrambot.repository

import org.bson.types.ObjectId
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.ReactiveHashOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import systems.ajax.englishstudytelegrambot.entity.Word


interface WordCashableRepository {

    fun saveNewWord(word: Word): Mono<Word>

    //fun updateWordTranslating(wordId: ObjectId, newWordTranslate: String): Mono<Word>

    fun deleteWord(wordId: ObjectId): Mono<Word>
//
//    fun isWordBelongsToLibraryByWordId(wordId: ObjectId, libraryId: ObjectId): Mono<Boolean>
//
//    fun isWordBelongsToLibrary(wordSpelling: String, libraryId: ObjectId): Mono<Boolean>

    fun getWord(wordId: ObjectId): Mono<Word>
//
//    fun getWordIdBySpellingAndLibraryId(wordSpelling: String, libraryId: ObjectId): Mono<ObjectId>
//
//    fun getAllWords(): Flux<Word>
//
//    fun getWordByLibraryNameTelegramUserIdWordSpelling(
//        libraryName: String,
//        telegramUserId: String,
//        wordSpelling: String
//    ): Mono<Word>
//
//    fun getWordIdByLibraryNameTelegramUserIdWordSpelling(
//        libraryName: String,
//        telegramUserId: String,
//        wordSpelling: String
//    ): Mono<ObjectId>
}

@Repository
class WordCashableRepositoryImpl(
    val redisTemplate: ReactiveRedisTemplate<String, Word>,
    val wordRepository: WordRepository,
) : WordCashableRepository {

    private val WORD_KEY = "WORDS_REDIS_KEY"

    override fun saveNewWord(word: Word): Mono<Word> =
        wordRepository.saveNewWord(word)
            .flatMap {
                redisTemplate.opsForValue().set(it.createWordKey(), word).thenReturn(it)
            }

//    override fun updateWordTranslating(wordId: ObjectId, newWordTranslate: String): Mono<Word> =
//        wordRepository.updateWordTranslating(wordId, newWordTranslate)
//            .flatMap { sh.set(WORD_KEY + it.id.toHexString(), it).thenReturn(it) }

    override fun deleteWord(wordId: ObjectId): Mono<Word> =
        wordRepository.deleteWord(wordId)
            .flatMap { redisTemplate.opsForValue().delete(it.createWordKey()).thenReturn(it) }

//    override fun isWordBelongsToLibraryByWordId(wordId: ObjectId, libraryId: ObjectId): Mono<Boolean> = TODO()
//      //  hashOps[wordId.toHexString(), HASH].map { it.libraryId == libraryId }
////            .handle { result, sink ->
////                if(result.not()){
////                    wordRepository.is
////                }
////            }
//
//    override fun isWordBelongsToLibrary(wordSpelling: String, libraryId: ObjectId): Mono<Boolean> = TODO()
////        getWordIdBySpellingAndLibraryId(wordSpelling, libraryId)
////            .flatMap { isWordBelongsToLibraryByWordId(it, libraryId) }

    override fun getWord(wordId: ObjectId): Mono<Word> {
        //val l = redisTemplate.opsForValue().get(wordId.createWordKey()).block()
        return redisTemplate.opsForValue().get(wordId.createWordKey())
            .doOnNext {
                println(it)
            }
            .switchIfEmpty(
                wordRepository.getWord(wordId).flatMap {
                    redisTemplate.opsForValue().set(it.createWordKey(), it).thenReturn(it)
                }
            )
    }

//    override fun getWordIdBySpellingAndLibraryId(wordSpelling: String, libraryId: ObjectId): Mono<ObjectId> {
//        TODO("Not yet implemented")
//    }
//
//    override fun getAllWords(): Flux<Word> {
//        TODO("Not yet implemented")
//    }
//
//    override fun getWordByLibraryNameTelegramUserIdWordSpelling(
//        libraryName: String,
//        telegramUserId: String,
//        wordSpelling: String
//    ): Mono<Word> {
//        TODO("Not yet implemented")
//    }
//
//    override fun getWordIdByLibraryNameTelegramUserIdWordSpelling(
//        libraryName: String,
//        telegramUserId: String,
//        wordSpelling: String
//    ): Mono<ObjectId> {
//        TODO("Not yet implemented")
//    }


    private fun Word.createWordKey(): String =
        id.createWordKey()

    private fun ObjectId.createWordKey(): String =
        WORD_KEY + this.toHexString()
}