package systems.ajax.application.ports.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import systems.ajax.application.ports.input.WordInPort
import systems.ajax.application.ports.output.AdditionalInfoAboutWordServiceOutPort
import systems.ajax.application.ports.output.UpdateWordEventSenderOutPort
import systems.ajax.application.ports.output.LibraryRepositoryOutPort
import systems.ajax.application.ports.output.WordRepositoryOutPort
import systems.ajax.domain.exception.LibraryNotFoundException
import systems.ajax.domain.exception.WordAlreadyPresentInLibraryException
import systems.ajax.domain.exception.WordNotFoundException
import systems.ajax.domain.model.AdditionalInfoAboutWord
import systems.ajax.domain.model.Word

@Service
class WordService(
    @Qualifier("wordCashableRepository") val wordRepository: WordRepositoryOutPort,
    val libraryRepository: LibraryRepositoryOutPort,
    val additionalInfoAboutWordService: AdditionalInfoAboutWordServiceOutPort,
    val updateWordEventSenderOutPort: UpdateWordEventSenderOutPort,
) : WordInPort {

    override fun saveNewWord(
        libraryName: String,
        telegramUserId: String,
        spelling: String,
        translate: String
    ): Mono<Word> =
        Mono.zip(
            findLibraryIdWithoutCertainSpelling(libraryName, telegramUserId, spelling),
            additionalInfoAboutWordService.findAdditionInfoAboutWord(spelling)
        ).flatMap { (libraryId, additionalInfoAboutWord) ->
            collectAndSaveWordInDb(spelling, translate, libraryId, additionalInfoAboutWord)
        }

    override fun updateWordTranslate(
        libraryName: String,
        telegramUserId: String,
        spelling: String,
        newTranslate: String
    ): Mono<Word> = wordRepository
        .getWordByLibraryNameTelegramUserIdWordSpelling(
            libraryName,
            telegramUserId,
            spelling
        )
        .switchIfEmpty {
            findLibraryId(libraryName, telegramUserId).handle { _, sink ->
                sink.error(WordNotFoundException("word is missing in library"))
            }
        }
        .flatMap { word -> wordRepository.updateWordTranslating(word.id, newTranslate) }
        .doOnNext{
            println(it)
        }
        .flatMap {
            sendUpdateWordEvent(it, libraryName, telegramUserId).thenReturn(it)
        }.doOnNext{
            println(it)
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

    override fun getAllWordsWithSpelling(spelling: String): Flux<Word> =
        wordRepository.getAllWordsWithSpelling(spelling)

    private fun findLibraryIdWithoutCertainSpelling(
        libraryName: String,
        telegramUserId: String,
        spelling: String,
    ): Mono<String> = findLibraryId(libraryName, telegramUserId)
        .doOnNext { log.info("library id = {}", it) }
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
        spelling: String,
        translate: String,
        libraryId: String,
        additionalInfoAboutWord: AdditionalInfoAboutWord,
    ): Mono<Word> = wordRepository
        .saveNewWord(
            Word("", spelling, translate, libraryId, additionalInfoAboutWord)
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

    private fun sendUpdateWordEvent(
        word: Word,
        libraryName: String,
        telegramUserId: String,
    ): Mono<Void> =
        updateWordEventSenderOutPort.sendEvent(word, libraryName, telegramUserId)

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
