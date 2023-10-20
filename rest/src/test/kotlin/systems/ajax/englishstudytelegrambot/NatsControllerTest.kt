package systems.ajax.englishstudytelegrambot

import io.nats.client.Connection
import io.nats.client.Message
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.remove
import systems.ajax.NatsSubject.Admin.GET_ALL_LIBRARIES_SUBJECT
import systems.ajax.NatsSubject.Admin.GET_ALL_USERS_SUBJECT
import systems.ajax.NatsSubject.Admin.GET_ALL_WORDS_SUBJECT
import systems.ajax.NatsSubject.Library.CREATE_NEW_LIBRARY_SUBJECT
import systems.ajax.NatsSubject.Library.DELETE_LIBRARY_SUBJECT
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.User
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.NatsSubject.Library.GET_ALL_WORDS_FROM_LIBRARY_SUBJECT
import systems.ajax.englishstudytelegrambot.dto.entity.LibraryDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.UserDtoResponse
import systems.ajax.englishstudytelegrambot.entity.AdditionalInfoAboutWord
import systems.ajax.englishstudytelegrambot.nats.mapper.toLibraryResponse
import systems.ajax.englishstudytelegrambot.nats.mapper.toWordResponse
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.WordRepository
import systems.ajax.englishstudytelegrambot.service.AdminService
import systems.ajax.response_request.admin.GetAllWordsRequest
import systems.ajax.response_request.admin.GetAllLibrariesRequest
import systems.ajax.response_request.admin.GetAllUsersRequest
import systems.ajax.response_request.admin.GetAllUsersResponse
import systems.ajax.response_request.admin.GetAllWordsResponse
import systems.ajax.response_request.admin.GetAllLibrariesResponse
import systems.ajax.response_request.library.CreateNewLibrary.CreateNewLibraryRequest
import systems.ajax.response_request.library.CreateNewLibrary.CreateNewLibraryResponse
import systems.ajax.response_request.library.DeleteLibrary.DeleteLibraryResponse
import systems.ajax.response_request.library.DeleteLibrary.DeleteLibraryRequest
import systems.ajax.response_request.library.GetAllWordsFromLibrary.GetAllWordsFromLibraryResponse
import systems.ajax.response_request.library.GetAllWordsFromLibrary.GetAllWordsFromLibraryRequest

import java.time.Duration

@SpringBootTest
class NatsControllerTest {

    @Autowired
    private lateinit var natsConnection: Connection

    @Autowired
    private lateinit var libraryRepository: LibraryRepository

    @Autowired
    private lateinit var wordRepository: WordRepository

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

    private fun addLibrariesAndUsers(): List<Library> =
        listOf(
            libraryRepository.saveNewLibrary("testLibraryName1", "testTelegramUserId1"),
            libraryRepository.saveNewLibrary("testLibraryName2", "testTelegramUserId2"),
            libraryRepository.saveNewLibrary("testLibraryName3", "testTelegramUserId1")
        )

    private fun createEmptyAdditionalInfoAboutWord() = AdditionalInfoAboutWord("", "", "", "")

    @Test
    fun testGetAllUserNatsController() {

        val libraries = addLibrariesAndUsers()

        val message: Message = doRequest(GET_ALL_USERS_SUBJECT, GetAllUsersRequest.getDefaultInstance().toByteArray())

        val telegramUserIdsList: List<String> =
            GetAllUsersResponse.parser().parseFrom(message.data).success.telegramUserIdsList

        //testTelegramUserId1, testTelegramUserId2
        Assertions.assertIterableEquals(
            adminService.getAllUsers().map(UserDtoResponse::telegramUserId), telegramUserIdsList
        )
    }

    @Test
    fun testGetAllLibrariesNatsController() {
        val libraries = addLibrariesAndUsers()

        val message = doRequest(GET_ALL_LIBRARIES_SUBJECT, GetAllLibrariesRequest.getDefaultInstance().toByteArray())
        val libraryList =
            GetAllLibrariesResponse.parser().parseFrom(
                message.data
            ).success.librariesList

        Assertions.assertIterableEquals(libraries.map(Library::toLibraryResponse), libraryList)
    }

    @Test
    fun testGetAllWordsNatsController() {
        val libraries = addLibrariesAndUsers()
        val words = addWords(libraries)

        val message = doRequest(GET_ALL_WORDS_SUBJECT, GetAllWordsRequest.getDefaultInstance().toByteArray())

        val wordsList = GetAllWordsResponse.parser().parseFrom(message.data).success.wordsList

        Assertions.assertEquals(words.map(Word::toWordResponse), wordsList)
    }

    @Test
    fun testGetAllWordsFromLibrariesNatsControllerEmptyLibrary() {

        val libraries = addLibrariesAndUsers()
        val words = addWords(libraries)

        val message = doRequest(
            GET_ALL_WORDS_FROM_LIBRARY_SUBJECT,
            GetAllWordsFromLibraryRequest.newBuilder()
                .setLibraryName(libraries[0].name)
                .setTelegramUserId(libraries[0].ownerId)
                .build().toByteArray()
        )

        val wordsFromLibraryList = GetAllWordsFromLibraryResponse.parser().parseFrom(message.data).success.wordsList

         Assertions.assertEquals(listOf(words[0], words[1]).map(Word::toWordResponse), wordsFromLibraryList)
    }

    @Test
    fun testGetAllWordsFromLibrariesNatsControllerNotEmptyLibrary() {
        val libraries = addLibrariesAndUsers()
        val words = addWords(libraries)

        val message = doRequest(
            GET_ALL_WORDS_FROM_LIBRARY_SUBJECT,
            GetAllWordsFromLibraryRequest.newBuilder()
                .setLibraryName(libraries[0].name)
                .setTelegramUserId(libraries[0].ownerId)
                .build().toByteArray()
        )

        val wordsFromLibraryList = GetAllWordsFromLibraryResponse.parser().parseFrom(message.data).success.wordsList

        Assertions.assertEquals(2, wordsFromLibraryList.size)
    }

    @Test
    fun testCreateNewLibraryNatsController() {
        val message = doRequest(
            CREATE_NEW_LIBRARY_SUBJECT,
            CreateNewLibraryRequest.newBuilder()
                .setLibraryName("testLibraryName1")
                .setTelegramUserId("testTelegramUserId1")
                .build().toByteArray()
        )

        val createdLibraryName: String =
            CreateNewLibraryResponse.parser().parseFrom(message.data).success.createdLibrary.name

        Assertions.assertEquals("testLibraryName1", createdLibraryName)
        Assertions.assertTrue(adminService.getAllLibraries().map(LibraryDtoResponse::name).contains(createdLibraryName))
    }

    @Test
    fun testCreateSeveralNewLibraryNatsController() {
        repeat(3) {
            doRequest(
                CREATE_NEW_LIBRARY_SUBJECT,
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
        val libraries: List<Library> = addLibrariesAndUsers()

        val message = doRequest(
            DELETE_LIBRARY_SUBJECT,
            DeleteLibraryRequest.newBuilder()
                .setLibraryName(libraries[0].name)
                .setTelegramUserId(libraries[0].ownerId)
                .build().toByteArray()
        )

        val deletedLibraryName: String =
            DeleteLibraryResponse.parser().parseFrom(message.data).success.deletedLibrary.name

        Assertions.assertEquals(libraries[0].name, deletedLibraryName)
        Assertions.assertFalse(adminService.getAllLibraries()
            .map(LibraryDtoResponse::toLibraryResponse)
            .contains(libraries[0].toLibraryResponse()))
    }

    private fun addWords(libraries: List<Library>): List<Word> =
        listOf(
            wordRepository.saveNewWord(
                Word(
                    spelling = "word1",
                    translate = "слово1",
                    libraryId = libraries[0].id,
                    additionalInfoAboutWord = createEmptyAdditionalInfoAboutWord()
                )
            ),
            wordRepository.saveNewWord(
                Word(
                    spelling = "word2",
                    translate = "слово2",
                    libraryId = libraries[0].id,
                    additionalInfoAboutWord = createEmptyAdditionalInfoAboutWord()
                )
            ),
            wordRepository.saveNewWord(
                Word(
                    spelling = "word1",
                    translate = "слово2",
                    libraryId = libraries[1].id,
                    additionalInfoAboutWord = createEmptyAdditionalInfoAboutWord()
                )
            )
        )

    private fun doRequest(subject: String, byteArray: ByteArray) =
        natsConnection.requestWithTimeout(
            subject,
            byteArray,
            Duration.ofSeconds(20L)
        ).get()
}
