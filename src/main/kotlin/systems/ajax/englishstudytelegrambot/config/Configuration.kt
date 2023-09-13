package systems.ajax.englishstudytelegrambot.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class Configuration {

    @Value("\${base.url.wordnikAPI}")
    lateinit var baseUrlToWordnikAPI: String

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient =
        builder
            .baseUrl(baseUrlToWordnikAPI)
            .build()
}
