package systems.ajax.englishstudytelegrambot.repository

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.englishstudytelegrambot.entity.Word

@Suppress("TooManyFunctions")
interface WordRepository {

    fun saveNewWord(word: Word): Mono<Word>

    fun updateWordTranslating(wordId: ObjectId, newWordTranslate: String): Mono<Word>

    fun deleteWord(wordId: ObjectId): Mono<Word>

    fun isWordBelongsToLibraryByWordId(wordId: ObjectId, libraryId: ObjectId): Mono<Boolean>

    fun isWordBelongsToLibrary(wordSpelling: String, libraryId: ObjectId): Mono<Boolean>

    fun getWord(wordId: ObjectId): Mono<Word>

    fun getWordIdBySpellingAndLibraryId(wordSpelling: String, libraryId: ObjectId): Mono<ObjectId>

    fun getAllWords(): Flux<Word>

    fun getWordByLibraryNameTelegramUserIdWordSpelling(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): Mono<Word>

    fun getWordIdByLibraryNameTelegramUserIdWordSpelling(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String
    ): Mono<ObjectId>

    fun getAllWordsFromLibrary(libraryId: ObjectId): Flux<Word>

    fun getAllWordsWithSpelling(wordSpelling: String): Flux<Word>
}
