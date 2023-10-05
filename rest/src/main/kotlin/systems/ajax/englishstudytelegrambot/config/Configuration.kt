package systems.ajax.englishstudytelegrambot.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.Builder
import systems.ajax.englishstudytelegrambot.property.WordnikProperties

@Configuration
@EnableConfigurationProperties(WordnikProperties::class)
class Configuration {

    @Bean
    fun webClient(builder: Builder, wordnikProperties: WordnikProperties): WebClient = builder
        .baseUrl(wordnikProperties.baseUrl)
        .build()
}
