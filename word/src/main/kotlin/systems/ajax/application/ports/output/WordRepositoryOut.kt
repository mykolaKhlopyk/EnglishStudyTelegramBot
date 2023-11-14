package systems.ajax.application.ports.output

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.domain.model.Word


@Suppress("TooManyFunctions")
interface WordRepositoryOut {

    fun saveNewWord(word: Word): Mono<Word>

    fun updateWordTranslating(wordId: String, newWordTranslate: String): Mono<Word>

    fun deleteWord(wordId: String): Mono<Word>

    fun isWordBelongsToLibraryByWordId(wordId: String, libraryId: String): Mono<Boolean>

    fun isWordBelongsToLibrary(wordSpelling: String, libraryId: String): Mono<Boolean>

    fun getWord(wordId: String): Mono<Word>

    fun getWordIdBySpellingAndLibraryId(wordSpelling: String, libraryId: String): Mono<String>

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
    ): Mono<String>

    fun getAllWordsFromLibrary(libraryId: String): Flux<Word>

    fun getAllWordsWithSpelling(spelling: String): Flux<Word>
}
