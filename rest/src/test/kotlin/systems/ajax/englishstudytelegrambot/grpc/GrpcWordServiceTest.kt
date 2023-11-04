package systems.ajax.englishstudytelegrambot.grpc

import LibrarySaverInMongoDbForTesting.saveLibraryForTesting
import WordSaverInMongoDbForTesting.saveWordForTesting
import io.grpc.ManagedChannelBuilder
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import reactor.kotlin.test.test
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.mapper.toWordResponse
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository
import systems.ajax.englishstudytelegrambot.repository.WordRepository
import systems.ajax.service.CreateWordDtoRequest
import systems.ajax.service.DeleteWordRequest
import systems.ajax.service.DeleteWordResponse
import systems.ajax.service.GetFullInfoAboutWordRequest
import systems.ajax.service.GetFullInfoAboutWordResponse
import systems.ajax.service.GetFullInfoAboutWordResponse.Success as GetFullInfoAboutWordSuccess
import systems.ajax.service.DeleteWordResponse.Success as DeleteWordSuccess
import systems.ajax.service.ReactorWordServiceGrpc
import systems.ajax.service.SaveNewWordRequest
import systems.ajax.service.SaveNewWordResponse
import systems.ajax.service.SaveNewWordResponse.Success as SaveWordSuccess
import systems.ajax.service.UpdateWordTranslateRequest
import systems.ajax.service.UpdateWordTranslateResponse
import systems.ajax.service.UpdateWordTranslateResponse.Success as UpdateWordSuccess


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GrpcWordServiceTest {

    @Autowired
    lateinit var libraryRepository: LibraryRepository

    @Autowired
    @Qualifier("wordRepositoryImpl")
    lateinit var wordRepository: WordRepository

    @Test
    fun `should save word`() {
        // GIVEN
        val library = libraryRepository.saveLibraryForTesting()
        val wordForSaving = WordFactory.createWord(library.id)

        // WHEN
        val savedWord = stub.saveNewWord(
            SaveNewWordRequest.newBuilder()
                .setLibraryName(library.name)
                .setTelegramUserId(library.ownerId)
                .setCreateWordDtoRequest(
                    CreateWordDtoRequest.newBuilder()
                        .setSpelling(wordForSaving.spelling)
                        .setTranslate(wordForSaving.translate)
                        .build()
                ).build()
        )
            .map(SaveNewWordResponse::getSuccess)
            .map(SaveWordSuccess::getWord).block()!!

        //THEN
        Assertions.assertEquals(wordForSaving.spelling, savedWord.spelling)
        wordRepository.getWord(ObjectId(savedWord.id))
            .map(Word::toWordResponse).test()
            .expectNext(savedWord).verifyComplete()
    }

    @Test
    fun `should update word`() {
        // GIVEN
        val library = libraryRepository.saveLibraryForTesting()
        val word = wordRepository.saveWordForTesting(library.id)
        val newTranslate = "${System.nanoTime()} newTranslate"

        // WHEN
        val updatedWord = stub.updateWordTranslate(
            UpdateWordTranslateRequest.newBuilder()
                .setLibraryName(library.name)
                .setTelegramUserId(library.ownerId)
                .setCreateWordDtoRequest(
                    CreateWordDtoRequest.newBuilder()
                        .setSpelling(word.spelling)
                        .setTranslate(newTranslate)
                        .build()
                ).build()
        )
            .map(UpdateWordTranslateResponse::getSuccess)
            .map(UpdateWordSuccess::getWord).block()!!

        //THEN
        Assertions.assertEquals(word.spelling, updatedWord.spelling)
        Assertions.assertEquals(newTranslate, updatedWord.translating)
        wordRepository.getWord(ObjectId(updatedWord.id))
            .map(Word::toWordResponse).test()
            .expectNext(updatedWord).verifyComplete()
    }

    @Test
    fun `should delete word`() {
        // GIVEN
        val library = libraryRepository.saveLibraryForTesting()
        val word = wordRepository.saveWordForTesting(library.id)

        // WHEN
        val deletedWord = stub.deleteWord(
            DeleteWordRequest.newBuilder()
                .setLibraryName(library.name)
                .setTelegramUserId(library.ownerId)
                .setWordSpelling(word.spelling)
                .build()
        )
            .map(DeleteWordResponse::getSuccess)
            .map(DeleteWordSuccess::getWord).block()!!

        //THEN
        Assertions.assertEquals(word.spelling, deletedWord.spelling)
        wordRepository.getWord(ObjectId(deletedWord.id))
            .test()
            .verifyComplete()
    }

    @Test
    fun `should get all info about word`() {
        // GIVEN
        val library = libraryRepository.saveLibraryForTesting()
        val word = wordRepository.saveWordForTesting(library.id)

        // WHEN //THEN
        stub.getFullInfoAboutWord(
            GetFullInfoAboutWordRequest.newBuilder()
                .setLibraryName(library.name)
                .setTelegramUserId(library.ownerId)
                .setWordSpelling(word.spelling)
                .build()
        )
            .map(GetFullInfoAboutWordResponse::getSuccess)
            .map(GetFullInfoAboutWordSuccess::getWord)
            .test()
            .expectNext(word.toWordResponse())
            .verifyComplete()
    }

    companion object {

        lateinit var stub: ReactorWordServiceGrpc.ReactorWordServiceStub

        @JvmStatic
        @BeforeAll
        fun setup() {

            val channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build()

            stub = ReactorWordServiceGrpc.newReactorStub(channel)
        }
    }
}
