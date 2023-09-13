package systems.ajax.englishstudytelegrambot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import systems.ajax.englishstudytelegrambot.service.WordinkKeyProperty
import systems.ajax.englishstudytelegrambot.service.WordnikLinkProperties

@Configuration
@EnableConfigurationProperties(WordnikLinkProperties::class, WordnikBaseUrlProperty::class, WordinkKeyProperty::class)
class Configuration {
    @Bean
    fun webClient(builder: WebClient.Builder, wordnikBaseUrlProperty: WordnikBaseUrlProperty): WebClient =
        builder
            .baseUrl(wordnikBaseUrlProperty.url)
            .build()
}

@ConfigurationProperties(prefix = "base")
data class WordnikBaseUrlProperty @ConstructorBinding constructor(
    val url: String
)
