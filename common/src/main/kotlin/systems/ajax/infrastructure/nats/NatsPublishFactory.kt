package systems.ajax.infrastructure.nats

import com.google.protobuf.GeneratedMessageV3
import io.nats.client.Connection
import io.nats.client.Message
import org.springframework.stereotype.Component

@Component
class NatsPublishFactory(val natsConnection: Connection) {

    fun publish(subject: String, message: GeneratedMessageV3): Unit =
        natsConnection.publish(
            subject,
            message.toByteArray()
        )
}
