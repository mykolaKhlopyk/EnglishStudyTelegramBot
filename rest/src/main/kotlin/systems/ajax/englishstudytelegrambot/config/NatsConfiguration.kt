package systems.ajax.englishstudytelegrambot.config

import io.nats.client.Connection
import io.nats.client.Nats
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import systems.ajax.englishstudytelegrambot.property.NatsProperties

@Configuration
class NatsConfiguration {
    @Bean
    fun natsConnection(natsProperties: NatsProperties): Connection =
        Nats.connect(natsProperties.serverPath)
}
