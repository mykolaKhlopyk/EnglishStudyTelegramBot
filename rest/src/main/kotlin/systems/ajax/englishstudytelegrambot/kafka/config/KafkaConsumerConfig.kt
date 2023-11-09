package systems.ajax.englishstudytelegrambot.kafka.config

import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import systems.ajax.response_request.word.UpdateWordEventOuterClass.UpdateWordEvent


@EnableKafka
@Configuration
class KafkaConsumerConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    val bootstrapServers: String,
    @Value("\${spring.kafka.schema-registry-url}")
    val schemaRegistryUrl: String
) {

    fun receiverOptions(): ReceiverOptions<String, UpdateWordEvent> =
        ReceiverOptions.create(getMapProperties())

    @Bean
    fun kafkaReceiverUpdatingWord(): KafkaReceiver<String, UpdateWordEvent> =
        KafkaReceiver.create(receiverOptions().subscription(listOf(KafkaTopics.UPDATED_WORD)))

    private fun getMapProperties(): MutableMap<String, Any> =
        mutableMapOf(
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "latest",
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to "group_id",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to KafkaProtobufDeserializer::class.java,
            KafkaProtobufDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG to schemaRegistryUrl,
            KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE to UpdateWordEvent::class.java
        )
}
