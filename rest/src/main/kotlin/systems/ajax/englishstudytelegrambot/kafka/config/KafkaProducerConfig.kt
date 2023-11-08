package systems.ajax.englishstudytelegrambot.kafka.config

import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializerConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.logging.log4j.message.SimpleMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import systems.ajax.response_request.word.EventUpdateWordOuterClass.EventUpdateWord
import java.util.*


@Configuration
class KafkaProducerConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    val bootstrapServers: String,
    @Value("\${spring.kafka.schema-registry-url}")
    val schemaRegistryUrl: String
) {

    @Bean
    fun producer(): Producer<String, EventUpdateWord> =
        KafkaProducer(getProperties())

    private fun getProperties() =
        Properties().apply {
            set(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
            set(ConsumerConfig.GROUP_ID_CONFIG, "group_id")
            set(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            set(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaProtobufSerializer::class.java.name)
            set(KafkaProtobufSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl)
        }
}
