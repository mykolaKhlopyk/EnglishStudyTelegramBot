package systems.ajax.englishstudytelegrambot.bpp

import com.google.protobuf.GeneratedMessageV3
import io.nats.client.Connection
import io.nats.client.Dispatcher
import io.nats.client.Message
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import systems.ajax.englishstudytelegrambot.nats.controller.NatsController

@Component
class NatsControllerHandlerBeanPostProcessor(val natsConnection: Connection) : BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        if (bean is NatsController<*, *>) {
            NatsControllerDecorator(bean, natsConnection).decorate()
        }
        return bean
    }
}

class NatsControllerDecorator<RequestType : GeneratedMessageV3, ResponseType : GeneratedMessageV3>(
    private val natsController: NatsController<RequestType, ResponseType>,
    private val natsConnection: Connection
) {

    fun decorate() {
        val dispatcher = createDispatcherForNatsController()
        addSubscribeToNutsController(dispatcher)
    }

    private fun createDispatcherForNatsController(): Dispatcher =
        natsConnection.createDispatcher { message: Message ->
            val response = createResponse(message)
            natsConnection.publish(message.replyTo, response.toByteArray())
        }

    private fun createResponse(message: Message): ResponseType {
        val parsedData = natsController.parser.parseFrom(message.data)
        return natsController.handle(parsedData)
    }

    private fun addSubscribeToNutsController(dispatcher: Dispatcher) {
        dispatcher.subscribe(natsController.subject)
    }
}
