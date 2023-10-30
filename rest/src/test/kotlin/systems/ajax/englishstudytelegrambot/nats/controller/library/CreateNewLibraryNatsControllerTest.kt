package systems.ajax.englishstudytelegrambot.nats.controller.library

import io.nats.client.Message
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.kotlin.test.test
import systems.ajax.NatsSubject
import systems.ajax.englishstudytelegrambot.nats.NatsRequestFactory
import systems.ajax.englishstudytelegrambot.nats.mapper.toLibraryResponse
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.entity.LibraryOuterClass
import systems.ajax.response_request.library.CreateNewLibrary

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
            CreateNewLibrary.CreateNewLibraryRequest.newBuilder()
                .setLibraryName(libraryName)
                .setTelegramUserId(userIdName)
                .build().toByteArray()
        )

        // THEN
        val createdLibrary: LibraryOuterClass.Library =
            CreateNewLibrary.CreateNewLibraryResponse.parser().parseFrom(message.data).success.createdLibrary
        assertEquals(libraryName, createdLibrary.name)

        libraryRepository.getLibraryById(ObjectId(createdLibrary.id))
            .map { it.toLibraryResponse() }.test()
            .expectNext(createdLibrary)
            .verifyComplete()
    }
}