package systems.ajax.infrastructure.nats

import io.nats.client.Message
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import systems.ajax.NatsSubject
import LibrarySaverInMongoDbForTesting.saveLibraryForTesting
import WordSaverInMongoDbForTesting.saveWordForTesting
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Qualifier
import systems.ajax.domain.model.Library
import systems.ajax.infrastructure.mongo.repository.LibraryRepository
import systems.ajax.infrastructure.mongo.repository.WordRepository
import systems.ajax.infrastructure.nats.mapper.toProto
import systems.ajax.response_request.library.DeleteLibraryRequest
import systems.ajax.response_request.library.DeleteLibraryResponse

@SpringBootTest
class DeleteLibraryNatsControllerTest {

    @Autowired
    private lateinit var natsRequestFactory: NatsRequestFactory

    @Autowired
    private lateinit var libraryRepository: LibraryRepository

    @Autowired
    @Qualifier("wordRepository")
    private lateinit var wordRepository: WordRepository

    @Test
    fun `should delete library with its words`() {
        // GIVEN
        val nanoTime = System.nanoTime().toString()
        val nameOfDeleteLibrary = "$nanoTime deletelibraryName"
        val nameOfSaveLibrary = "$nanoTime savelibraryName"
        val telegramUserId = "$nanoTime userIdName"

        val libraryForDeleting: Library = libraryRepository.saveLibraryForTesting(nameOfDeleteLibrary, telegramUserId)
        val libraryForSaving: Library = libraryRepository.saveNewLibrary(nameOfSaveLibrary, telegramUserId)
            .block()!!

        wordRepository.saveWordForTesting(libraryForDeleting.id)
        wordRepository.saveWordForTesting(libraryForDeleting.id)
        wordRepository.saveWordForTesting(libraryForSaving.id)

        // WHEN
        val message: Message = natsRequestFactory.doRequest(
            NatsSubject.Library.DELETE_LIBRARY_SUBJECT,
            DeleteLibraryRequest.newBuilder()
                .setLibraryName(nameOfDeleteLibrary)
                .setTelegramUserId(telegramUserId)
                .build()
        )

        // THEN
        assertEquals(
            libraryForDeleting.toProto(),
            DeleteLibraryResponse.parser().parseFrom(message.data).success.deletedLibrary
        )

        assertTrue(libraryRepository.getLibraryById(libraryForDeleting.id).block() == null)
        assertTrue(wordRepository.getAllWordsFromLibrary(libraryForDeleting.id).collectList().block()!!.isEmpty())

        assertTrue(libraryRepository.getLibraryById(libraryForSaving.id).block() != null)
        assertFalse(wordRepository.getAllWordsFromLibrary(libraryForSaving.id).collectList().block()!!.isEmpty())
    }
}
