package systems.ajax.englishstudytelegrambot.repository

import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

object CommonQuery {

    fun queryToFindLibraryByNameAndTelegramUserId(
        telegramUserId: String,
        nameOfLibraryForDeleting: String
    ) = Query.query(
        Criteria().andOperator(
            Criteria.where("ownerId").`is`(telegramUserId),
            Criteria.where("name").`is`(nameOfLibraryForDeleting)
        )
    )
}
