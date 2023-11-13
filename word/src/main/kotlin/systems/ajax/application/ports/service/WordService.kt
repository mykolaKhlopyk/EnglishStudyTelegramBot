package systems.ajax.application.ports.service

import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import systems.ajax.application.ports.input.WordServiceIn
import systems.ajax.application.ports.output.LibraryRepositoryOut
import systems.ajax.application.ports.output.WordRepositoryOut
import systems.ajax.domain.exception.LibraryNotFoundException
import systems.ajax.domain.exception.WordAlreadyPresentInLibraryException
import systems.ajax.domain.exception.WordNotFoundException
import systems.ajax.domain.model.AdditionalInfoAboutWord
import systems.ajax.domain.model.Word
import systems.ajax.infrastructure.kafka.config.KafkaTopics
import systems.ajax.infrastructure.rest.dto.request.CreateWordDtoRequest
import systems.ajax.response_request.word.UpdateWordEvent

@Service
class WordService(
    @Qualifier("wordCashableRepository") val wordRepository: WordRepositoryOut,
    val libraryRepository: LibraryRepositoryOut,
    val additionalInfoAboutWordService: AdditionalInfoAboutWordService,
    val sender: KafkaSender<String, UpdateWordEvent>,
) : WordServiceIn {

    override fun saveNewWord(
        libraryName: String,
        telegramUserId: String,
        createWordDtoRequest: CreateWordDtoRequest,
    ): Mono<Word> =
        Mono.zip(
            findLibraryIdWithoutCertainSpelling(libraryName, telegramUserId, createWordDtoRequest.spelling),
            additionalInfoAboutWordService.findAdditionInfoAboutWord(createWordDtoRequest.spelling)
        ).flatMap { (libraryId, additionalInfoAboutWord) ->
            collectAndSaveWordInDb(createWordDtoRequest, libraryId, additionalInfoAboutWord)
        }

    override fun updateWordTranslate(
        libraryName: String,
        telegramUserId: String,
        createWordDtoRequest: CreateWordDtoRequest,
    ): Mono<Word> = wordRepository
        .getWordByLibraryNameTelegramUserIdWordSpelling(
            libraryName,
            telegramUserId,
            createWordDtoRequest.spelling
        )
        .switchIfEmpty {
            findLibraryId(libraryName, telegramUserId).handle { _, sink ->
                sink.error(WordNotFoundException("word is missing in library"))
            }
        }
        .flatMap { word -> wordRepository.updateWordTranslating(word.id, createWordDtoRequest.translate) }
        .flatMap {
            sendUpdateWordEventTKafka(it, libraryName, telegramUserId)
        }

    override fun deleteWord(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String,
    ): Mono<Word> = wordRepository
        .getWordByLibraryNameTelegramUserIdWordSpelling(
            libraryName,
            telegramUserId,
            wordSpelling
        )
        .switchIfEmpty(
            findLibraryId(libraryName, telegramUserId)
                .handle { _, sink ->
                    sink.error(WordNotFoundException("spelling = $wordSpelling"))
                }
        )
        .doOnNext { id -> log.info("id of deleted word is {}", id) }
        .map(Word::id)
        .flatMap { wordRepository.deleteWord(it) }

    override fun getFullInfoAboutWord(
        libraryName: String,
        telegramUserId: String,
        wordSpelling: String,
    ): Mono<Word> = wordRepository
        .getWordByLibraryNameTelegramUserIdWordSpelling(libraryName, telegramUserId, wordSpelling)
        .switchIfEmpty {
            findLibraryId(libraryName, telegramUserId)
                .handle { _, sink ->
                    sink.error(WordNotFoundException("spelling = $wordSpelling"))
                }
        }

    override fun getAllWordsFromLibrary(libraryName: String, telegramUserId: String): Flux<Word> =
        libraryRepository.getLibraryIdByLibraryNameAndTelegramUserId(libraryName, telegramUserId)
            .flatMapMany(wordRepository::getAllWordsFromLibrary)


    private fun findLibraryIdWithoutCertainSpelling(
        libraryName: String,
        telegramUserId: String,
        spelling: String,
    ): Mono<String> = findLibraryId(libraryName, telegramUserId)
        .doOnNext { log.info("library id = {}", it) }
        // .doOnNext{ log.info("is word not belongs = {}", isWordNotBelongsToLibrary(it, spelling).block()!!)}
        .filterWhen { libraryId ->
            isWordNotBelongsToLibrary(libraryId, spelling)
        }
        .switchIfEmpty(
            Mono.error(WordAlreadyPresentInLibraryException("word $spelling is present in library $libraryName"))
        )


    private fun findLibraryId(
        libraryName: String,
        telegramUserId: String,
    ): Mono<String> = libraryRepository
        .getLibraryIdByLibraryNameAndTelegramUserId(
            libraryName,
            telegramUserId
        )
        .switchIfEmpty(
            Mono.error(LibraryNotFoundException("libraryName = $libraryName and telegramUserId = $telegramUserId"))
        )

    private fun collectAndSaveWordInDb(
        createWordDtoRequest: CreateWordDtoRequest,
        libraryId: String,
        additionalInfoAboutWord: AdditionalInfoAboutWord,
    ): Mono<Word> = wordRepository
        .saveNewWord(
            Word(
                "",
                createWordDtoRequest.spelling,
                createWordDtoRequest.translate,
                libraryId,
                additionalInfoAboutWord
            )
        )

    private fun isWordNotBelongsToLibrary(
        libraryId: String,
        wordSpelling: String,
    ): Mono<Boolean> = wordRepository
        .isWordBelongsToLibrary(
            wordSpelling,
            libraryId
        ).map {
            it.not()
        }

    private fun sendUpdateWordEventTKafka(
        it: Word,
        libraryName: String,
        telegramUserId: String,
    ): Mono<Word> =
        sender.send(createRecord(it, libraryName, telegramUserId).toMono()).then(it.toMono())

    private fun createRecord(
        it: Word,
        libraryName: String,
        telegramUserId: String,
    ): SenderRecord<String, UpdateWordEvent, Nothing> =
        SenderRecord.create(
            ProducerRecord(
                KafkaTopics.UPDATED_WORD, it.libraryId,
                UpdateWordEvent.newBuilder()
                    .setLibraryName(libraryName)
                    .setTelegramUserId(telegramUserId)
                    .setWordSpelling(it.spelling)
                    .setNewWordTranslate(it.translate)
                    .build()
            ),
            null
        )

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
