package systems.ajax.infrastructure.rest

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.application.ports.input.WordServiceIn
import systems.ajax.domain.model.Word
import systems.ajax.infrastructure.rest.dto.request.CreateWordDtoRequest
import systems.ajax.infrastructure.rest.dto.response.WordDtoResponse
import systems.ajax.infrastructure.rest.dto.response.toDtoResponse

@RestController
@RequestMapping("/api/word")
class WordController(
    private val wordService: WordServiceIn
) {

    @GetMapping("/{libraryName}/fullInfo")
    fun getAdditionalInfoAboutWord(
        @PathVariable("libraryName") libraryName: String,
        @RequestHeader("telegramUserId") telegramUserId: String,
        @RequestHeader("wordSpelling") wordSpelling: String
    ): Mono<WordDtoResponse> =
        wordService.getFullInfoAboutWord(libraryName, telegramUserId, wordSpelling).map(Word::toDtoResponse)

    @GetMapping("/{libraryName}")
    fun getAllWordsFromLibrary(
        @PathVariable("libraryName") libraryName: String,
        @RequestHeader("telegramUserId") telegramUserId: String
    ): Flux<WordDtoResponse> =
        wordService.getAllWordsFromLibrary(libraryName, telegramUserId).map(Word::toDtoResponse)

    @PostMapping("/{libraryName}")
    fun saveWordInLibrary(
        @PathVariable("libraryName") libraryName: String,
        @RequestHeader("telegramUserId") telegramUserId: String,
        @RequestBody createWordDtoRequest: CreateWordDtoRequest
    ): Mono<WordDtoResponse> =
        wordService.saveNewWord(libraryName, telegramUserId, createWordDtoRequest).map(Word::toDtoResponse)

    @PatchMapping("/{libraryName}")
    fun updateWordTranslateInLibrary(
        @PathVariable("libraryName") libraryName: String,
        @RequestHeader("telegramUserId") telegramUserId: String,
        @RequestBody createWordDtoRequest: CreateWordDtoRequest
    ): Mono<WordDtoResponse> =
        wordService.updateWordTranslate(libraryName, telegramUserId, createWordDtoRequest).map(Word::toDtoResponse)

    @DeleteMapping("/{libraryName}")
    fun deleteWordFromLibrary(
        @PathVariable("libraryName") libraryName: String,
        @RequestHeader("telegramUserId") telegramUserId: String,
        @RequestHeader("wordSpelling") wordSpelling: String
    ): Mono<WordDtoResponse> =
        wordService.deleteWord(libraryName, telegramUserId, wordSpelling).map(Word::toDtoResponse)
}
