package systems.ajax.englishstudytelegrambot.nats.controller.admin

import io.nats.client.Message
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.kotlin.test.test
import systems.ajax.NatsSubject
import systems.ajax.englishstudytelegrambot.nats.NatsRequestFactory
import systems.ajax.englishstudytelegrambot.nats.controller.LibrarySaverInDbForTesting.saveLibraryForTesting
import systems.ajax.englishstudytelegrambot.nats.controller.WordSaverInDbForTesting.saveWordForTesting
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.WordRepository
import systems.ajax.response_request.admin.GetAllWordsRequest
import systems.ajax.response_request.admin.GetAllWordsResponse

@SpringBootTest
class GetAllWordsNatsControllerTest {

    @Autowired
    private lateinit var natsRequestFactory: NatsRequestFactory

    @Autowired
    private lateinit var libraryRepository: LibraryRepository

    @Autowired
    private lateinit var wordRepository: WordRepository

    @Test
    fun testGetAllWordsNatsController() {
        // GIVEN
        val nanoTime = System.nanoTime().toString()
        val libraryName1 = "$nanoTime libraryName1"
        val libraryName2 = "$nanoTime libraryName2"
        val telegramUserId = "$nanoTime userIdName"
        val wordSpelling1 = "$nanoTime spelling1"
        val wordSpelling2 = "$nanoTime spelling2"
        val wordSpelling3 = "$nanoTime spelling3"

        val library1 = libraryRepository.saveLibraryForTesting(libraryName1, telegramUserId)
        val library2 = libraryRepository.saveLibraryForTesting(libraryName2, telegramUserId)
        val word1 = wordRepository.saveWordForTesting(library1.id, wordSpelling1)
        val word2 = wordRepository.saveWordForTesting(library1.id, wordSpelling2)
        val word3 = wordRepository.saveWordForTesting(library2.id, wordSpelling3)

        // WHEN
        val message: Message = natsRequestFactory.doRequest(
                NatsSubject.Admin.GET_ALL_WORDS_SUBJECT,
                GetAllWordsRequest.getDefaultInstance().toByteArray()
            )

        // THEN
        val words = GetAllWordsResponse.parser().parseFrom(message.data).success.wordsList
            .filter { it.spelling.contains(nanoTime) }
        assertEquals(3, words.size)
        wordRepository.getAllWords().filter { it.spelling.contains(nanoTime) }.test()
            .expectNext(word1)
            .expectNext(word2)
            .expectNext(word3)
            .verifyComplete()
    }
}
