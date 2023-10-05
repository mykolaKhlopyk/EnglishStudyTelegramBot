package systems.ajax.englishstudytelegrambot.nats

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import io.nats.client.Connection

interface NatsController<RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> {

    val subject: String

    val connection: Connection

    val parser: Parser<RequestT>

    fun handle(request: RequestT): ResponseT
}