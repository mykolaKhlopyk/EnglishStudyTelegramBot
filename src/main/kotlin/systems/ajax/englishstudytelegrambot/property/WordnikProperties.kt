package systems.ajax.englishstudytelegrambot.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "base")
data class WordnikBaseUrlProperty @ConstructorBinding constructor(
    val url: String
)

@ConfigurationProperties(prefix = "wordnik.api")
data class WordinkKeyProperty @ConstructorBinding constructor(val key: String)

@ConfigurationProperties(prefix = "link")
data class WordnikLinkProperties @ConstructorBinding constructor(
    val audio: String,
    val definition: String,
    val example: String,
    val pronunciation: String
)
