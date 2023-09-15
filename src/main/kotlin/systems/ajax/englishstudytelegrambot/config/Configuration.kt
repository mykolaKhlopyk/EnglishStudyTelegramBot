package systems.ajax.englishstudytelegrambot.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import systems.ajax.englishstudytelegrambot.property.*

@Configuration
@EnableConfigurationProperties(WordnikLinkProperties::class, WordnikBaseUrlProperty::class, WordinkKeyProperty::class)
class Configuration {
    @Bean
    fun webClient(builder: WebClient.Builder, wordnikBaseUrlProperty: WordnikBaseUrlProperty): WebClient =
        builder
            .baseUrl(wordnikBaseUrlProperty.baseUrl)
            .build()
}
