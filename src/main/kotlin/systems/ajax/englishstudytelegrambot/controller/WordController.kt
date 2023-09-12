package systems.ajax.englishstudytelegrambot.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import systems.ajax.englishstudytelegrambot.entity.AdditionalInfoAboutWord
import systems.ajax.englishstudytelegrambot.service.AdditionalInfoAboutWordService

@RestController
@RequestMapping("/api")
class WordController(val additionalInfoAboutWordService: AdditionalInfoAboutWordService) {

    @GetMapping("/{word}")
    suspend fun getAdditionalInfoAboutWord(@PathVariable("word") wordSpelling: String): AdditionalInfoAboutWord =
        additionalInfoAboutWordService.findAdditionInfoAboutWord(wordSpelling)


}
