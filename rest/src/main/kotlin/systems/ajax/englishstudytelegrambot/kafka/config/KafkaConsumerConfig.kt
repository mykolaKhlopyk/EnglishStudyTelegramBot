package systems.ajax.englishstudytelegrambot.kafka.config

import com.squareup.wire.get
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import systems.ajax.response_request.word.EventUpdateWordOuterClass.EventUpdateWord
import java.util.*

@EnableKafka
@Configuration
class KafkaConsumerConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    lateinit var bootstrapServers: String

    @Bean
    fun consumer(): KafkaConsumer<String, EventUpdateWord> {
        val consumer =  KafkaConsumer<String, EventUpdateWord>(getProperties())
        consumer.subscribe(listOf("mytopic"))
        return consumer
    }

    fun getProperties(): Properties =
        Properties().apply {
            set(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,  bootstrapServers)
            set(ConsumerConfig.GROUP_ID_CONFIG,"group_id")
            set(KafkaProtobufDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8085")
            set(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            set(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer::class.java)
            set(KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE, EventUpdateWord::class.java)
        }
}
