package systems.ajax.englishstudytelegrambot.nats

import io.nats.client.Connection
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class NatsRequestFactory(val natsConnection: Connection) {
    fun doRequest(subject: String, byteArray: ByteArray) =
        natsConnection.requestWithTimeout(
            subject,
            byteArray,
            Duration.ofSeconds(20L)
        ).get()
}