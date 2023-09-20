package systems.ajax.englishstudytelegrambot.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "wordnik")
class WordnikProperties {
    lateinit var baseUrl: String
    lateinit var tokenKey: String
    lateinit var link: Link

    class Link {
        lateinit var audioSourceLink: String
        lateinit var definitionOfWordLink: String
        lateinit var examplesOfUsingWordInSentencesLink: String
        lateinit var correctPronunciationOfWordLink: String
    }
}
