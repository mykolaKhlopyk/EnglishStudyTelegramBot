package systems.ajax.infrastructure.config

import systems.ajax.infrastructure.property.NatsProperties
import io.nats.client.Connection
import io.nats.client.Nats
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(NatsProperties::class)
@Configuration
class NatsConfiguration {

    @Bean
    fun natsConnection(natsProperties: NatsProperties): Connection =
        Nats.connect(natsProperties.serverPath)
}
