package systems.ajax.infrastructure.external.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.Builder
import systems.ajax.infrastructure.property.WordnikProperties

@Configuration
@EnableConfigurationProperties(WordnikProperties::class)
class WordnikConfiguration {

    @Bean
    fun webClient(builder: Builder, wordnikProperties: WordnikProperties): WebClient = builder
        .baseUrl(wordnikProperties.baseUrl)
        .build()
}
