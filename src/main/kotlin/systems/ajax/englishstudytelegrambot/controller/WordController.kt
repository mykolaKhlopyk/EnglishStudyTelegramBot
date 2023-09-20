package systems.ajax.englishstudytelegrambot.controller

import jakarta.validation.constraints.NotBlank
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import systems.ajax.englishstudytelegrambot.annotation.LogMethodsByRequiredAnnotations
import systems.ajax.englishstudytelegrambot.entity.AdditionalInfoAboutWord
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.service.AdditionalInfoAboutWordService
import systems.ajax.englishstudytelegrambot.service.WordService

@RestController
@RequestMapping("/api")
@LogMethodsByRequiredAnnotations(LogMethodsByRequiredAnnotations::class, PostMapping::class)
class WordController(
    val additionalInfoAboutWordService: AdditionalInfoAboutWordService,
    val wordService: WordService
) {

    @LogMethodsByRequiredAnnotations
    @GetMapping("addInfo/{word}")
    suspend fun getAdditionalInfoAboutWord(
        @NotBlank @PathVariable("word") wordSpelling: String
    ): AdditionalInfoAboutWord =
        additionalInfoAboutWordService.findAdditionInfoAboutWord(wordSpelling)

    @PostMapping("/{wordSpelling}/{wordTranslate}")
    suspend fun saveWord(
        @NotBlank @PathVariable("wordSpelling") wordSpelling: String,
        @NotBlank @PathVariable("wordTranslate") wordTranslate: String
    ): Word = wordService.save(wordSpelling, wordTranslate)

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
