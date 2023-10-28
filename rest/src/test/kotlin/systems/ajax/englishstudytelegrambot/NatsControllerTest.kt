package systems.ajax.englishstudytelegrambot

import io.nats.client.Connection
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import reactor.test.StepVerifier
import systems.ajax.NatsSubject.Admin.GET_ALL_LIBRARIES_SUBJECT
import systems.ajax.NatsSubject.Admin.GET_ALL_USERS_SUBJECT
import systems.ajax.NatsSubject.Admin.GET_ALL_WORDS_SUBJECT
import systems.ajax.NatsSubject.Library.CREATE_NEW_LIBRARY_SUBJECT
import systems.ajax.NatsSubject.Library.DELETE_LIBRARY_SUBJECT
import systems.ajax.NatsSubject.Library.GET_ALL_WORDS_FROM_LIBRARY_SUBJECT
import systems.ajax.englishstudytelegrambot.dto.entity.toDtoResponse
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.entity.AdditionalInfoAboutWord
import systems.ajax.englishstudytelegrambot.nats.mapper.toLibraryResponse
import systems.ajax.englishstudytelegrambot.nats.mapper.toWordResponse
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.UserRepository
import systems.ajax.englishstudytelegrambot.repository.WordRepository
import systems.ajax.englishstudytelegrambot.service.AdminService
import systems.ajax.entity.LibraryOuterClass.Library
import systems.ajax.response_request.admin.GetAllLibrariesRequest
import systems.ajax.response_request.admin.GetAllLibrariesResponse
import systems.ajax.response_request.admin.GetAllUsersRequest
import systems.ajax.response_request.admin.GetAllUsersResponse
import systems.ajax.response_request.admin.GetAllWordsResponse
import systems.ajax.response_request.admin.GetAllWordsRequest
import systems.ajax.response_request.library.CreateNewLibrary.CreateNewLibraryRequest
import systems.ajax.response_request.library.CreateNewLibrary.CreateNewLibraryResponse
import systems.ajax.response_request.library.DeleteLibrary.DeleteLibraryResponse
import systems.ajax.response_request.library.DeleteLibrary.DeleteLibraryRequest
import systems.ajax.response_request.library.GetAllWordsFromLibrary.GetAllWordsFromLibraryRequest
import systems.ajax.response_request.library.GetAllWordsFromLibrary.GetAllWordsFromLibraryResponse

import java.time.Duration

@SpringBootTest
class NatsControllerTest {

    @Autowired
    private lateinit var natsConnection: Connection

    @Autowired
    private lateinit var libraryRepository: LibraryRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var wordRepository: WordRepository

    @Autowired
    private lateinit var adminService: AdminService

    private fun createEmptyAdditionalInfoAboutWord() = AdditionalInfoAboutWord("", "", "", "")

    @Test
    fun testGetAllUserNatsController() {
        // GIVEN
        val nanoTime = System.nanoTime().toString()
        val libraryName1 = "$nanoTime libraryName1"
        val libraryName2 = "$nanoTime libraryName2"
        val telegramUserId1 = "$nanoTime userIdName1"
        val telegramUserId2 = "$nanoTime userIdName2"

        val library1 = libraryRepository.saveNewLibrary(libraryName1, telegramUserId1).block()
        val library2 = libraryRepository.saveNewLibrary(libraryName2, telegramUserId2).block()

        // WHEN
        val message = doRequest(GET_ALL_USERS_SUBJECT, GetAllUsersRequest.getDefaultInstance().toByteArray())
        val telegramUserIdsList: List<String> =
            GetAllUsersResponse.parser().parseFrom(message.data).success.telegramUserIdsList

        //THEN
        StepVerifier.create(adminService.getAllUsers().map { it.telegramUserId }.filter { it.contains(nanoTime) })
            .expectNext(telegramUserId1)
            .expectNext(telegramUserId2)
            .verifyComplete()
    }

    @Test
    fun testGetAllLibrariesNatsController() {
        // GIVEN
        val nanoTime = System.nanoTime().toString()
        val libraryName1 = "$nanoTime libraryName1"
        val libraryName2 = "$nanoTime libraryName2"
        val telegramUserId = "$nanoTime userIdName"

        val library1 = libraryRepository.saveNewLibrary(libraryName1, telegramUserId).block()
        val library2 = libraryRepository.saveNewLibrary(libraryName2, telegramUserId).block()

        // WHEN
        val message = doRequest(GET_ALL_LIBRARIES_SUBJECT, GetAllLibrariesRequest.getDefaultInstance().toByteArray())
        val libraryList =
            GetAllLibrariesResponse.parser().parseFrom(
                message.data
            ).success.librariesList

        //THEN
        Assertions.assertTrue(libraryList.filter { it.name.contains(nanoTime) }.size == 2)
        StepVerifier.create(adminService.getAllLibraries().filter { it.name.contains(nanoTime) }.map { it })
            .expectNext(library1!!.toDtoResponse())
            .expectNext(library2!!.toDtoResponse())
            .verifyComplete()
    }

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

        val library = libraryRepository.saveNewLibrary(libraryName1, telegramUserId).block()
        val word1 = wordRepository.saveNewWord(
            Word(
                spelling = wordSpelling1,
                translate = "translate",
                libraryId = library!!.id,
                additionalInfoAboutWord = createEmptyAdditionalInfoAboutWord()
            )
        ).block()
        val word2 = wordRepository.saveNewWord(
            Word(
                spelling = wordSpelling2,
                translate = "translate",
                libraryId = library!!.id,
                additionalInfoAboutWord = createEmptyAdditionalInfoAboutWord()
            )
        ).block()
        val word3 = wordRepository.saveNewWord(
            Word(
                spelling = wordSpelling3,
                translate = "translate",
                libraryId = library!!.id,
                additionalInfoAboutWord = createEmptyAdditionalInfoAboutWord()
            )
        ).block()

        // WHEN
        val message = doRequest(GET_ALL_WORDS_SUBJECT, GetAllWordsRequest.getDefaultInstance().toByteArray())
        val wordsFromLibrary = GetAllWordsResponse.parser().parseFrom(message.data).success.wordsList

        // THEN
        StepVerifier.create(adminService.getAllWords().filter { it.spelling.contains(nanoTime) })
            .expectNext(word1!!.toDtoResponse())
            .expectNext(word2!!.toDtoResponse())
            .expectNext(word3!!.toDtoResponse())
            .verifyComplete()
    }

    @Test
    fun `should return empty list when library doesnt contain words`() {
        // GIVEN
        val nanoTime = System.nanoTime().toString()
        val libraryName = "$nanoTime libraryName"
        val telegramUserId = "$nanoTime userIdName"

        val library = libraryRepository.saveNewLibrary(libraryName, telegramUserId).block()

        // WHEN
        val message = doRequest(
            GET_ALL_WORDS_FROM_LIBRARY_SUBJECT,
            GetAllWordsFromLibraryRequest.newBuilder()
                .setLibraryName(libraryName)
                .setTelegramUserId(telegramUserId)
                .build().toByteArray()
        )
        val wordsFromLibrary = GetAllWordsFromLibraryResponse.parser().parseFrom(message.data).success.wordsList

        // THEN
        Assertions.assertEquals(0, wordsFromLibrary.size)
        StepVerifier.create(adminService.getAllWords().filter { it.spelling.contains(nanoTime) }.map { it })
            .verifyComplete()
    }

    @Test
    fun `should return all words from library`() {
        // GIVEN
        val nanoTime = System.nanoTime().toString()
        val libraryName = "$nanoTime libraryName"
        val telegramUserId = "$nanoTime userIdName"
        val wordSpelling1 = "$nanoTime spelling1"
        val wordSpelling2 = "$nanoTime spelling2"

        val library = libraryRepository.saveNewLibrary(libraryName, telegramUserId).block()
        val word1 = wordRepository.saveNewWord(
            Word(
                spelling = wordSpelling1,
                translate = "translate",
                libraryId = library!!.id,
                additionalInfoAboutWord = createEmptyAdditionalInfoAboutWord()
            )
        ).block()
        val word2 = wordRepository.saveNewWord(
            Word(
                spelling = wordSpelling2,
                translate = "translate",
                libraryId = library!!.id,
                additionalInfoAboutWord = createEmptyAdditionalInfoAboutWord()
            )
        ).block()
        // WHEN

        val message = doRequest(
            GET_ALL_WORDS_FROM_LIBRARY_SUBJECT,
            GetAllWordsFromLibraryRequest.newBuilder()
                .setLibraryName(libraryName)
                .setTelegramUserId(telegramUserId)
                .build().toByteArray()
        )
        val wordsFromLibrary = GetAllWordsFromLibraryResponse.parser().parseFrom(message.data).success.wordsList

        // THEN
        Assertions.assertEquals(2, wordsFromLibrary.size)
        StepVerifier.create(adminService.getAllWords().filter { it.spelling.contains(nanoTime) }.map { it })
            .expectNext(word1!!.toDtoResponse())
            .expectNext(word2!!.toDtoResponse())
            .verifyComplete()
    }

    @Test
    fun `should create new library`() {
        // GIVEN
        val nanoTime = System.nanoTime().toString()
        val libraryName = "$nanoTime libraryName"
        val userIdName = "$nanoTime userIdName"

        // WHEN
        val message = doRequest(
            CREATE_NEW_LIBRARY_SUBJECT,
            CreateNewLibraryRequest.newBuilder()
                .setLibraryName(libraryName)
                .setTelegramUserId(userIdName)
                .build().toByteArray()
        )
        val createdLibrary: Library =
            CreateNewLibraryResponse.parser().parseFrom(message.data).success.createdLibrary

        // THEN
        Assertions.assertEquals(libraryName, createdLibrary.name)
        StepVerifier.create(
            libraryRepository.getLibraryById(ObjectId(createdLibrary.id))
                .map { it.toLibraryResponse() }
        )
            .expectNext(createdLibrary)
            .verifyComplete()
    }

    @Test
    fun `should delete library with its words`() {
        // GIVEN
        val nanoTime = System.nanoTime().toString()
        val nameOfDeleteLibrary = "$nanoTime deletelibraryName"
        val nameOfSaveLibrary = "$nanoTime savelibraryName"
        val telegramUserId = "$nanoTime userIdName"
        val wordSpelling1 = "$nanoTime spelling1"
        val wordSpelling2 = "$nanoTime spelling2"
        val wordSpelling3 = "$nanoTime spelling3"

        val libraryForDeleting = libraryRepository.saveNewLibrary(nameOfDeleteLibrary, telegramUserId).block()
        val libraryForSaving = libraryRepository.saveNewLibrary(nameOfSaveLibrary, telegramUserId).block()
        wordRepository.saveNewWord(
            Word(
                spelling = wordSpelling1,
                translate = "translate",
                libraryId = libraryForDeleting!!.id,
                additionalInfoAboutWord = createEmptyAdditionalInfoAboutWord()
            )
        ).block()
        wordRepository.saveNewWord(
            Word(
                spelling = wordSpelling2,
                translate = "translate",
                libraryId = libraryForDeleting!!.id,
                additionalInfoAboutWord = createEmptyAdditionalInfoAboutWord()
            )
        ).block()
        val wordWhichShouldBeForSaving = wordRepository.saveNewWord(
            Word(
                spelling = wordSpelling3,
                translate = "translate",
                libraryId = libraryForSaving!!.id,
                additionalInfoAboutWord = createEmptyAdditionalInfoAboutWord()
            )
        ).block()

        // WHEN
        val message = doRequest(
            DELETE_LIBRARY_SUBJECT,
            DeleteLibraryRequest.newBuilder()
                .setLibraryName(nameOfDeleteLibrary)
                .setTelegramUserId(telegramUserId)
                .build().toByteArray()
        )
        val deletedLibrary: Library =
            DeleteLibraryResponse.parser().parseFrom(message.data).success.deletedLibrary

        // THEN
        Assertions.assertEquals(libraryForDeleting.toLibraryResponse(), deletedLibrary)
        StepVerifier.create(userRepository.getAllLibrariesOfUser(telegramUserId).filter { it.name.contains(nanoTime) })
            .expectNext(libraryForSaving)
            .verifyComplete()
        StepVerifier.create(adminService.getAllWords().filter { it.spelling.contains(nanoTime) }.map { it })
            .expectNext(wordWhichShouldBeForSaving!!.toDtoResponse())
            .verifyComplete()
    }

    private fun doRequest(subject: String, byteArray: ByteArray) =
        natsConnection.requestWithTimeout(
            subject,
            byteArray,
            Duration.ofSeconds(20L)
        ).get()
}
