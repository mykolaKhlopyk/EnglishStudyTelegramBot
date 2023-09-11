package systems.ajax.englishstudytelegrambot.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import systems.ajax.englishstudytelegrambot.service.AdditionalInfoAboutWordService

@RestController
@RequestMapping("/api")
class WordController(val additionalInfoAboutWordService: AdditionalInfoAboutWordService){

    val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/{word}")
    suspend fun getAdditionalInfoAboutWord(@PathVariable("word") wordSpelling: String){
        log.info("res = {}", additionalInfoAboutWordService.findAdditionInfoAboutWord(wordSpelling))
    }

}
