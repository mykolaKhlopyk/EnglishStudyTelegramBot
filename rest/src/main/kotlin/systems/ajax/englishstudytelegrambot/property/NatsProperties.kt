package systems.ajax.englishstudytelegrambot.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "nats")
class NatsProperties {
    lateinit var serverPath: String
}
