package systems.ajax.application.ports.input

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.domain.model.Word

interface WordServiceIn {

    fun saveNewWord(
        libraryName: String,
        telegramUserId: String,
        spelling: String,
        translate: String
    ): Mono<Word>

    fun getAllWordsFromLibrary(
        libraryName: String,
        telegramUserId: String,
    ): Flux<Word>


    fun updateWordTranslate(
        libraryName: String,
        telegramUserId: String,
        spelling: String,
        newTranslate: String
    ): Mono<Word>

    fun deleteWord(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String,
    ): Mono<Word>

    fun getFullInfoAboutWord(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String,
    ): Mono<Word>

    fun getAllWordsWithSpelling(spelling: String): Flux<Word>
}
