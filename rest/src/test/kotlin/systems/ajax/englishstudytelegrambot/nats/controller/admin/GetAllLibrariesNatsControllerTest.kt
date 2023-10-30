package systems.ajax.englishstudytelegrambot.nats.controller.admin

import io.nats.client.Message
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.kotlin.test.test
import reactor.test.StepVerifier
import systems.ajax.NatsSubject
import systems.ajax.englishstudytelegrambot.dto.entity.toDtoResponse
import systems.ajax.englishstudytelegrambot.nats.NatsRequestFactory
import systems.ajax.englishstudytelegrambot.nats.controller.LibrarySaverInDbForTesting.saveLibraryForTesting
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.UserRepository
import systems.ajax.response_request.admin.GetAllLibrariesRequest
import systems.ajax.response_request.admin.GetAllLibrariesResponse

@SpringBootTest
class GetAllLibrariesNatsControllerTest {

    @Autowired
    private lateinit var libraryRepository: LibraryRepository

    @Autowired
    private lateinit var natsRequestFactory: NatsRequestFactory

    @Test
    fun testGetAllLibrariesNatsController() {
        // GIVEN
        val nanoTime = System.nanoTime().toString()
        val libraryName1 = "$nanoTime libraryName1"
        val libraryName2 = "$nanoTime libraryName2"
        val telegramUserId = "$nanoTime userIdName"

        val library1 = libraryRepository.saveLibraryForTesting(libraryName1, telegramUserId)
        val library2 = libraryRepository.saveLibraryForTesting(libraryName2, telegramUserId)

        // WHEN
        val message: Message = natsRequestFactory.doRequest(
            NatsSubject.Admin.GET_ALL_LIBRARIES_SUBJECT,
            GetAllLibrariesRequest.getDefaultInstance().toByteArray()
        )

        //THEN
        val libraryList =
            GetAllLibrariesResponse.parser().parseFrom(
                message.data
            ).success.librariesList

        assertTrue(libraryList.filter { it.name.contains(nanoTime) }.size == 2)

        libraryRepository.getAllLibraries().filter { it.name.contains(nanoTime) }.test()
            .expectNext(library1)
            .expectNext(library2)
            .verifyComplete()
    }

}