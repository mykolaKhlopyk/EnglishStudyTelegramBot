package systems.ajax.englishstudytelegrambot.nats.controller.admin

import io.nats.client.Message
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.kotlin.test.test
import systems.ajax.NatsSubject
import systems.ajax.englishstudytelegrambot.nats.NatsRequestFactory
import LibrarySaverInMongoDbForTesting.saveLibraryForTesting
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.UserRepository
import systems.ajax.response_request.admin.GetAllUsersRequest
import systems.ajax.response_request.admin.GetAllUsersResponse

@SpringBootTest
class GetAllUsersNatsControllerTest {

    @Autowired
    private lateinit var natsRequestFactory: NatsRequestFactory

    @Autowired
    private lateinit var libraryRepository: LibraryRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should return all users`() {
        // GIVEN
        val nanoTime = System.nanoTime().toString()
        val libraryName1 = "$nanoTime libraryName1"
        val libraryName2 = "$nanoTime libraryName2"
        val telegramUserId1 = "$nanoTime userIdName1"
        val telegramUserId2 = "$nanoTime userIdName2"

        libraryRepository.saveLibraryForTesting(libraryName1, telegramUserId1)
        libraryRepository.saveLibraryForTesting(libraryName2, telegramUserId2)

        // WHEN
        val message: Message = natsRequestFactory.doRequest(
            NatsSubject.Admin.GET_ALL_USERS_SUBJECT,
            GetAllUsersRequest.getDefaultInstance().toByteArray()
        )

        //THEN
        val telegramUserIdsList: List<String> =
            GetAllUsersResponse.parser().parseFrom(message.data).success.telegramUserIdsList
                .filter { it.contains(nanoTime) }
        println(telegramUserIdsList)
        assertIterableEquals(listOf(telegramUserId1, telegramUserId2), telegramUserIdsList)

        userRepository.getAllUsers().map { it.telegramUserId }.filter { it.contains(nanoTime) }.test()
            .expectNext(telegramUserId1)
            .expectNext(telegramUserId2)
            .verifyComplete()
    }
}
