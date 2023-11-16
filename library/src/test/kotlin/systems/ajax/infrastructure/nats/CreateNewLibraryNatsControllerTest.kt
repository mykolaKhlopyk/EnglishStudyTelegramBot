package systems.ajax.infrastructure.nats

import io.nats.client.Message
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.kotlin.test.test
import systems.ajax.NatsSubject
import systems.ajax.domain.model.Library
import systems.ajax.infrastructure.mongo.repository.LibraryRepository
import systems.ajax.infrastructure.nats.mapper.toModel
import systems.ajax.response_request.library.CreateNewLibraryRequest
import systems.ajax.response_request.library.CreateNewLibraryResponse

@SpringBootTest
class CreateNewLibraryNatsControllerTest {

    @Autowired
    private lateinit var natsRequestFactory: NatsRequestFactory

    @Autowired
    private lateinit var libraryRepository: LibraryRepository

    @Test
    fun `should create new library`() {
        // GIVEN
        val nanoTime = System.nanoTime().toString()
        val libraryName = "$nanoTime libraryName"
        val userIdName = "$nanoTime userIdName"

        // WHEN
        val message: Message = natsRequestFactory.doRequest(
            NatsSubject.Library.CREATE_NEW_LIBRARY_SUBJECT,
            CreateNewLibraryRequest.newBuilder()
                .setLibraryName(libraryName)
                .setTelegramUserId(userIdName)
                .build()
        )

        // THEN
        val createdLibrary: Library =
            CreateNewLibraryResponse.parser().parseFrom(message.data).success.createdLibrary.toModel()
        assertEquals(libraryName, createdLibrary.name)

        libraryRepository.getLibraryById(createdLibrary.id)
            .test()
            .expectNext(createdLibrary)
            .verifyComplete()
    }
}
