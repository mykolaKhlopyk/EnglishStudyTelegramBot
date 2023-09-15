package systems.ajax.englishstudytelegrambot.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "wordnik")
data class WordnikBaseUrlProperty @ConstructorBinding constructor(
    val baseUrl: String
)

@ConfigurationProperties(prefix = "wordnik")
data class WordinkKeyProperty @ConstructorBinding constructor(val tokenKey: String)

@ConfigurationProperties(prefix = "wordnik.link")
data class WordnikLinkProperties @ConstructorBinding constructor(
    val audioSourceLink: String,
    val definitionOfWordLink: String,
    val examplesOfUsingWordInSentencesLink: String,
    val correctPronunciationOfWordLink: String
)
