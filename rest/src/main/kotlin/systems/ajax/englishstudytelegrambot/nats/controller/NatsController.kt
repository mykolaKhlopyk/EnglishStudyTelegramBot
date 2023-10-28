package systems.ajax.englishstudytelegrambot.nats.controller

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import reactor.core.publisher.Mono

interface NatsController<RequestType : GeneratedMessageV3, ResponseType : GeneratedMessageV3> {

    val subject: String

    val parser: Parser<RequestType>

    fun handle(request: RequestType): Mono<ResponseType>
}
