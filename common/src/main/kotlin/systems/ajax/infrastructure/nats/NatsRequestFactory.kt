package systems.ajax.infrastructure.nats

import com.google.protobuf.GeneratedMessageV3
import io.nats.client.Connection
import io.nats.client.Message
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class NatsRequestFactory(val natsConnection: Connection) {

    @Suppress("MagicNumber")
    fun doRequest(subject: String, message: GeneratedMessageV3): Message =
        natsConnection.requestWithTimeout(
            subject,
            message.toByteArray(),
            Duration.ofSeconds(20L)
        ).get()
}
