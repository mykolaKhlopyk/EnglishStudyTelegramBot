package systems.ajax.englishstudytelegrambot.nats.controller.library

import io.nats.client.Message
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import systems.ajax.NatsSubject
import systems.ajax.englishstudytelegrambot.nats.NatsRequestFactory
import LibrarySaverInMongoDbForTesting.saveLibraryForTesting
import WordSaverInMongoDbForTesting.saveWordForTesting
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.WordRepository
import systems.ajax.response_request.library.GetAllWordsFromLibrary

@SpringBootTest
class GetAllWordsFromLibraryNatsControllerTest {

    @Autowired
    private lateinit var natsRequestFactory: NatsRequestFactory

    @Autowired
    private lateinit var libraryRepository: LibraryRepository

    @Autowired
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
            GetAllWordsFromLibrary.GetAllWordsFromLibraryRequest.newBuilder()
                .setLibraryName(libraryName)
                .setTelegramUserId(telegramUserId)
                .build().toByteArray()
        )

        // THEN
        val wordsFromLibrary =
            GetAllWordsFromLibrary.GetAllWordsFromLibraryResponse.parser().parseFrom(message.data).success.wordsList
        assertEquals(2, wordsFromLibrary.size)
        assertTrue(libraryRepository.getAllWordsFromLibrary(library.id).collectList().block()!!.let {
            it.size == 2 && it.containsAll(listOf(word1, word2))
        })

    }
}
