package systems.ajax.englishstudytelegrambot.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import systems.ajax.englishstudytelegrambot.entity.Word


@Configuration
class RedisConfiguration {

    @Bean
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, Word> {
        val keySerializer = StringRedisSerializer()
        val objectMapper = ObjectMapper().findAndRegisterModules()
        val valueSerializer = Jackson2JsonRedisSerializer(objectMapper, Word::class.java)
        val context = RedisSerializationContext.newSerializationContext<String, Word>(keySerializer)
            .value(valueSerializer)
            .build()
        return ReactiveRedisTemplate(factory, context)
    }
}
