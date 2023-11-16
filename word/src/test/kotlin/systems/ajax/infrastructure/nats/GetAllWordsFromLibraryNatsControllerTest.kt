package systems.ajax.infrastructure.nats

import io.nats.client.Message
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import systems.ajax.NatsSubject
import LibrarySaverInMongoDbForTesting.saveLibraryForTesting
import WordSaverInMongoDbForTesting.saveWordForTesting
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Qualifier
import systems.ajax.infrastructure.mongo.repository.LibraryRepository
import systems.ajax.infrastructure.mongo.repository.WordRepository
import systems.ajax.response_request.library.GetAllWordsFromLibraryRequest
import systems.ajax.response_request.library.GetAllWordsFromLibraryResponse

@SpringBootTest
class GetAllWordsFromLibraryNatsControllerTest {

    @Autowired
    private lateinit var natsRequestFactory: NatsRequestFactory

    @Autowired
    private lateinit var libraryRepository: LibraryRepository

    @Autowired
    @Qualifier("wordRepository")
    private lateinit var wordRepository: WordRepository

    @Test
    fun `should return all words from library`() {
        // GIVEN
        val nanoTime = System.nanoTime().toString()
        val libraryName = "$nanoTime libraryName"
        val telegramUserId = "$nanoTime userIdName"

        val library = libraryRepository.saveLibraryForTesting(libraryName, telegramUserId)
        val word1 = wordRepository.saveWordForTesting(library.id)
        val word2 = wordRepository.saveWordForTesting(library.id)

        // WHEN
        val message: Message = natsRequestFactory.doRequest(
            NatsSubject.Library.GET_ALL_WORDS_FROM_LIBRARY_SUBJECT,
            GetAllWordsFromLibraryRequest.newBuilder()
                .setLibraryName(libraryName)
                .setTelegramUserId(telegramUserId)
                .build()
        )

        // THEN
        val wordsFromLibrary =
            GetAllWordsFromLibraryResponse.parser().parseFrom(message.data).success.wordsList
        assertEquals(2, wordsFromLibrary.size)
        assertTrue(wordRepository.getAllWordsFromLibrary(library.id).collectList().block()!!.let {
            it.size == 2 && it.containsAll(listOf(word1, word2))
        })
    }
}
