package systems.ajax.englishstudytelegrambot.service

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.englishstudytelegrambot.dto.request.CreateWordDtoRequest
import systems.ajax.englishstudytelegrambot.entity.Word

interface WordService {

    fun saveNewWord(
        libraryName: String,
        telegramUserId: String,
        createWordDtoRequest: CreateWordDtoRequest,
    ): Mono<Word>

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

    fun getAllWordsWithSpelling(wordSpelling: String): Flux<Word>
}
