package systems.ajax.englishstudytelegrambot.nats

import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class NatsPublishFactory(val natsConnection: Connection) {

    @Suppress("MagicNumber")
    fun publish(subject: String, byteArray: ByteArray): Unit =
        natsConnection.publish(
            subject,
            byteArray
        )
}
