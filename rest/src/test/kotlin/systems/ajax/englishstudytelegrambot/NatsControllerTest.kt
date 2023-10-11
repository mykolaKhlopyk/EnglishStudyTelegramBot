package systems.ajax.englishstudytelegrambot

import io.nats.client.Connection
import io.nats.client.Message
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.remove
import response_request.CreateNewLibrary.CreateNewLibraryRequest
import response_request.CreateNewLibrary.CreateNewLibraryResponse
import response_request.DeleteLibrary
import response_request.DeleteLibrary.DeleteLibraryRequest
import response_request.DeleteLibrary.DeleteLibraryResponse
import response_request.GetAllLibraries.GetAllLibrariesRequest
import response_request.GetAllLibraries.GetAllLibrariesResponse
import response_request.GetAllUsers.GetAllUsersRequest
import response_request.GetAllUsers.GetAllUsersResponse
import response_request.GetAllWords.GetAllWordsRequest
import response_request.GetAllWords.GetAllWordsResponse
import response_request.GetAllWordsFromLibrary.GetAllWordsFromLibraryRequest
import response_request.GetAllWordsFromLibrary.GetAllWordsFromLibraryResponse
import systems.ajax.NatsSubject.CREATE_NEW_LIBRARY
import systems.ajax.NatsSubject.DELETE_LIBRARY
import systems.ajax.NatsSubject.GET_ALL_LIBRARIES_SUBJECT
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.User
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.service.LibraryService
import systems.ajax.NatsSubject.GET_ALL_USERS_SUBJECT
import systems.ajax.NatsSubject.GET_ALL_WORDS_FROM_LIBRARY
import systems.ajax.NatsSubject.GET_ALL_WORDS_SUBJECT
import systems.ajax.englishstudytelegrambot.controller.AdminController
import systems.ajax.englishstudytelegrambot.dto.WordDto
import systems.ajax.englishstudytelegrambot.service.AdminService
import systems.ajax.englishstudytelegrambot.service.WordService
import java.time.Duration

@SpringBootTest
class NatsControllerTest {

    @Autowired
    private lateinit var natsConnection: Connection

    @Autowired
    private lateinit var libraryService: LibraryService

    @Autowired
    private lateinit var wordService: WordService

    @Autowired
    private lateinit var adminService: AdminService

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    fun clearDBs() {
        mongoTemplate.remove<User>(Query())
        mongoTemplate.remove<Library>(Query())
        mongoTemplate.remove<Word>(Query())
    }

    @Test
    fun testGetAllUserNatsController() {
        libraryService.createNewLibrary("testLibraryName1", "testTelegramUserId1")
        libraryService.createNewLibrary("testLibraryName2", "testTelegramUserId2")
        libraryService.createNewLibrary("testLibraryName3", "testTelegramUserId1")

        val message: Message = doRequest(GET_ALL_USERS_SUBJECT, GetAllUsersRequest.getDefaultInstance().toByteArray())
        val telegramUserIdsList: List<String> = GetAllUsersResponse.parser().parseFrom(message.data).telegramUserIdsList

        //testTelegramUserId1, testTelegramUserId2
        Assertions.assertEquals(
            2, telegramUserIdsList.size
        )
    }

    @Test
    fun testGetAllLibrariesNatsController() {
        libraryService.createNewLibrary("testLibraryName1", "testTelegramUserId1")
        libraryService.createNewLibrary("testLibraryName2", "testTelegramUserId2")
        libraryService.createNewLibrary("testLibraryName3", "testTelegramUserId1")

        val message = doRequest(GET_ALL_LIBRARIES_SUBJECT, GetAllLibrariesRequest.getDefaultInstance().toByteArray())
        val libraryList =
            GetAllLibrariesResponse.parser().parseFrom(
                message.data
            ).librariesList

        //testLibraryName1, testLibraryName2, testLibraryName3
        Assertions.assertEquals(3, libraryList.size)
    }

    @Test
    fun testGetAllWordsNatsController() = runBlocking {
        libraryService.createNewLibrary("testLibraryName1", "testTelegramUserId1")
        libraryService.createNewLibrary("testLibraryName2", "testTelegramUserId2")

        wordService.saveNewWord("testLibraryName1", "testTelegramUserId1", WordDto("word1", "переклад1"))
        wordService.saveNewWord("testLibraryName1", "testTelegramUserId1", WordDto("word2", "переклад2"))

        val message = doRequest(GET_ALL_WORDS_SUBJECT, GetAllWordsRequest.getDefaultInstance().toByteArray())

        val wordsList = GetAllWordsResponse.parser().parseFrom(message.data).wordsList

        //testLibraryName1, testLibraryName2, testLibraryName3
        Assertions.assertEquals(2, wordsList.size)
    }

    @Test
    fun testGetAllWordsFromLibrariesNatsControllerEmptyLibrary() = runBlocking {
        libraryService.createNewLibrary("testLibraryName1", "testTelegramUserId1")
        libraryService.createNewLibrary("testLibraryName2", "testTelegramUserId2")

        wordService.saveNewWord("testLibraryName1", "testTelegramUserId1", WordDto("word1", "переклад1"))
        wordService.saveNewWord("testLibraryName1", "testTelegramUserId1", WordDto("word2", "переклад2"))

        val message = doRequest(
            GET_ALL_WORDS_FROM_LIBRARY,
            GetAllWordsFromLibraryRequest.newBuilder()
                .setLibraryName("testLibraryName2")
                .setTelegramUserId("testTelegramUserId2")
                .build().toByteArray()
        )

        val wordsFromLibraryList = GetAllWordsFromLibraryResponse.parser().parseFrom(message.data).wordsList

        //word1, word2
        Assertions.assertEquals(0, wordsFromLibraryList.size)
    }

    @Test
    fun testGetAllWordsFromLibrariesNatsControllerNotEmptyLibrary() = runBlocking {
        libraryService.createNewLibrary("testLibraryName1", "testTelegramUserId1")
        libraryService.createNewLibrary("testLibraryName2", "testTelegramUserId2")

        wordService.saveNewWord("testLibraryName1", "testTelegramUserId1", WordDto("word1", "переклад1"))
        wordService.saveNewWord("testLibraryName1", "testTelegramUserId1", WordDto("word2", "переклад2"))
        wordService.saveNewWord("testLibraryName2", "testTelegramUserId2", WordDto("word3", "переклад3"))

        val message = doRequest(
            GET_ALL_WORDS_FROM_LIBRARY,
            GetAllWordsFromLibraryRequest.newBuilder()
                .setLibraryName("testLibraryName1")
                .setTelegramUserId("testTelegramUserId1")
                .build().toByteArray()
        )

        val wordsFromLibraryList = GetAllWordsFromLibraryResponse.parser().parseFrom(message.data).wordsList

        //word1, word2
        Assertions.assertEquals(2, wordsFromLibraryList.size)
    }

    @Test
    fun testCreateNewLibraryNatsController() {
        val message = doRequest(
            CREATE_NEW_LIBRARY,
            CreateNewLibraryRequest.newBuilder()
                .setLibraryName("testLibraryName1")
                .setTelegramUserId("testTelegramUserId1")
                .build().toByteArray()
        )

        val createdLibraryName: String = CreateNewLibraryResponse.parser().parseFrom(message.data).createdLibrary.name

        Assertions.assertEquals("testLibraryName1", createdLibraryName)
        Assertions.assertEquals(1, adminService.getAllLibraries().size)
    }

    @Test
    fun testCreateSeveralNewLibraryNatsController() {
        repeat(3){
            doRequest(
                CREATE_NEW_LIBRARY,
                CreateNewLibraryRequest.newBuilder()
                    .setLibraryName("testLibraryName$it")
                    .setTelegramUserId("testTelegramUserId1")
                    .build().toByteArray()
            )
        }

        Assertions.assertEquals(3, adminService.getAllLibraries().size)
    }

    @Test
    fun testDeleteLibraryNatsController() {
        libraryService.createNewLibrary("testLibraryName1", "testTelegramUserId1")
        libraryService.createNewLibrary("testLibraryName2", "testTelegramUserId2")

        val message = doRequest(
            DELETE_LIBRARY,
            DeleteLibraryRequest.newBuilder()
                .setLibraryName("testLibraryName1")
                .setTelegramUserId("testTelegramUserId1")
                .build().toByteArray()
        )

        val deletedLibraryName: String = DeleteLibraryResponse.parser().parseFrom(message.data).deletedLibrary.name

        Assertions.assertEquals("testLibraryName1", deletedLibraryName)
        Assertions.assertEquals(1, adminService.getAllLibraries().size)
    }

    private fun doRequest(subject: String, byteArray: ByteArray) =
        natsConnection.requestWithTimeout(
            subject,
            byteArray,
            Duration.ofSeconds(20L)
        ).get()

}
