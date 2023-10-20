package systems.ajax.englishstudytelegrambot.controller

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.DeleteMapping
import systems.ajax.englishstudytelegrambot.annotation.LogMethodsByRequiredAnnotations
import systems.ajax.englishstudytelegrambot.dto.entity.WordDtoResponse
import systems.ajax.englishstudytelegrambot.dto.request.CreateWordDtoRequest
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.service.WordService

@RestController
@RequestMapping("/api/word")
@LogMethodsByRequiredAnnotations(LogMethodsByRequiredAnnotations::class, PostMapping::class)
class WordController(
    val wordService: WordService
) {

    @LogMethodsByRequiredAnnotations
    @GetMapping("/{libraryName}/fullInfo")
    fun getAdditionalInfoAboutWord(
        @PathVariable("libraryName") libraryName: String,
        @RequestHeader("telegramUserId") telegramUserId: String,
        @RequestHeader("wordSpelling") wordSpelling: String
    ): WordDtoResponse =
        wordService.getFullInfoAboutWord(libraryName, telegramUserId, wordSpelling)

    @PostMapping("/{libraryName}")
    suspend fun saveWordInLibrary(
        @PathVariable("libraryName") libraryName: String,
        @RequestHeader("telegramUserId") telegramUserId: String,
        @RequestBody createWordDtoRequest: CreateWordDtoRequest
    ): WordDtoResponse = wordService.saveNewWord(libraryName, telegramUserId, createWordDtoRequest)

    @PatchMapping("/{libraryName}")
    fun updateWordTranslateInLibrary(
        @PathVariable("libraryName") libraryName: String,
        @RequestHeader("telegramUserId") telegramUserId: String,
        @RequestBody createWordDtoRequest: CreateWordDtoRequest
    ): WordDtoResponse = wordService.updateWordTranslate(libraryName, telegramUserId, createWordDtoRequest)

    @DeleteMapping("/{libraryName}")
    fun deleteWordFromLibrary(
        @PathVariable("libraryName") libraryName: String,
        @RequestHeader("telegramUserId") telegramUserId: String,
        @RequestHeader("wordSpelling") wordSpelling: String
    ): WordDtoResponse = wordService.deleteWord(libraryName, telegramUserId, wordSpelling)
}
