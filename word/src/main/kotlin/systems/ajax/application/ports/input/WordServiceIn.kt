package systems.ajax.application.ports.input

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.domain.model.Word
import systems.ajax.infrastructure.rest.dto.request.CreateWordDtoRequest

interface WordServiceIn {

    fun saveNewWord(
        libraryName: String,
        telegramUserId: String,
        createWordDtoRequest: CreateWordDtoRequest,
    ): Mono<Word>

    fun getAllWordsFromLibrary(
        libraryName: String,
        telegramUserId: String,
    ): Flux<Word>


    fun updateWordTranslate(
        libraryName: String,
        telegramUserId: String,
        createWordDtoRequest: CreateWordDtoRequest,
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
}
