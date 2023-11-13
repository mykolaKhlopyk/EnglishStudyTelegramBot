package systems.ajax.infrastructure.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "nats")
class NatsProperties @ConstructorBinding constructor(
    val serverPath: String
)
