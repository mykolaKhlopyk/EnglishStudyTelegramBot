package systems.ajax.englishstudytelegrambot.repository.impl

import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import systems.ajax.englishstudytelegrambot.annotation.LogMethodsByRequiredAnnotations
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.User
import systems.ajax.englishstudytelegrambot.repository.UserRepository


@Repository
@LogMethodsByRequiredAnnotations(LogMethodsByRequiredAnnotations::class)
class UserRepositoryImpl(val mongoTemplate: ReactiveMongoTemplate) : UserRepository {

    @LogMethodsByRequiredAnnotations
    override fun getAllUsers(): Flux<User> =
        mongoTemplate.find<User>(
            Query().apply {
                fields()
                    .include("ownerId")
                    .exclude("_id")
            }, "libraries"
        ).distinct()

    override fun getAllLibrariesOfUser(telegramUserId: String): Flux<Library> =
        mongoTemplate.find<Library>(
            Query.query(Criteria.where("ownerId").`is`(telegramUserId))
        )
}
