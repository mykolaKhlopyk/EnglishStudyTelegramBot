package systems.ajax.infrastructure.nats

import io.nats.client.Connection
import io.nats.client.Message
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class NatsRequestFactory(val natsConnection: Connection) {

    @Suppress("MagicNumber")
    fun doRequest(subject: String, byteArray: ByteArray): Message =
        natsConnection.requestWithTimeout(
            subject,
            byteArray,
            Duration.ofSeconds(20L)
        ).get()
}
