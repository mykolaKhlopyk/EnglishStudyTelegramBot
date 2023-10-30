package systems.ajax.englishstudytelegrambot.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/redis")
class HelloRedisController {
    @Autowired
    private lateinit var template: RedisTemplate<String, String>

    private val STRING_KEY_PREFIX = "redi2read:strings:"

    @PostMapping("/strings")
    @ResponseStatus(HttpStatus.CREATED)
    fun setString(@RequestBody kvp: Map.Entry<String, String>): Map.Entry<String, String> {
        println(kvp)
        template.opsForValue().set(STRING_KEY_PREFIX + kvp.key, kvp.value);
        return kvp
    }

    @GetMapping("/strings")
    fun getStr(): String {
        return template.opsForValue().get("redi2read:strings:database:redis:creator") ?: "Missing"
    }
}
