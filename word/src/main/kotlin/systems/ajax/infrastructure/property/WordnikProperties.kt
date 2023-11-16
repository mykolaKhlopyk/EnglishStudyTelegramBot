package systems.ajax.infrastructure.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "wordnik")
class WordnikProperties @ConstructorBinding constructor(
    val baseUrl: String,
    val tokenKey: String,
    val link: Link,
) {

    class Link @ConstructorBinding constructor(
        val audioSourceLink: String,
        val definitionOfWordLink: String,
        val examplesOfUsingWordInSentencesLink: String,
        val correctPronunciationOfWordLink: String,
    )
}
