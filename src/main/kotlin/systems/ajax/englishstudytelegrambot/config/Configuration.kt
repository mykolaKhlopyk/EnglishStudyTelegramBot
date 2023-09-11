package systems.ajax.englishstudytelegrambot.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class Configuration {

    @Bean
    fun webClient(builder: WebClient.Builder) :WebClient =
        builder
            .baseUrl("https://api.wordnik.com/v4")
            .build()

}
