package systems.ajax.infrastructure.grpc

import LibrarySaverInMongoDbForTesting.saveLibraryForTesting
import WordSaverInMongoDbForTesting.saveWordForTesting
import io.grpc.ManagedChannelBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import reactor.kotlin.test.test
import systems.ajax.domain.model.Word
import systems.ajax.infrastructure.mongo.repository.LibraryRepository
import systems.ajax.infrastructure.mongo.repository.WordRepository
import systems.ajax.infrastructure.nats.mapper.toProto
import systems.ajax.service.CreateWordDtoRequest
import systems.ajax.service.DeleteWordRequest
import systems.ajax.service.DeleteWordResponse
import systems.ajax.service.GetFullInfoAboutWordRequest
import systems.ajax.service.GetFullInfoAboutWordResponse
import systems.ajax.service.ReactorWordServiceGrpc
import systems.ajax.service.SaveNewWordRequest
import systems.ajax.service.SaveNewWordResponse
import systems.ajax.service.UpdateWordTranslateRequest
import systems.ajax.service.UpdateWordTranslateResponse
import systems.ajax.service.GetFullInfoAboutWordResponse.Success as GetFullInfoAboutWordSuccess
import systems.ajax.service.DeleteWordResponse.Success as DeleteWordSuccess
import systems.ajax.service.SaveNewWordResponse.Success as SaveWordSuccess
import systems.ajax.service.UpdateWordTranslateResponse.Success as UpdateWordSuccess

@SpringBootTest
class GrpcWordServiceTest {

    @Autowired
    lateinit var libraryRepository: LibraryRepository

    @Autowired
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
            .map(SaveWordSuccess::getWord)
            .block()!!

        //THEN
        Assertions.assertEquals(wordForSaving.spelling, savedWord.spelling)
        wordRepository.getWord(savedWord.id)
            .map(Word::toProto).test()
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
        wordRepository.getWord(updatedWord.id)
            .map(Word::toProto).test()
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
        wordRepository.getWord(deletedWord.id)
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
            .expectNext(word.toProto())
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
