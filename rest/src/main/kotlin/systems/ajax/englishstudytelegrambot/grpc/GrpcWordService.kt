package systems.ajax.englishstudytelegrambot.grpc

import net.devh.boot.grpc.server.service.GrpcService
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.englishstudytelegrambot.mapper.toWordResponse
import systems.ajax.englishstudytelegrambot.dto.request.CreateWordDtoRequest as ServiceCreateWordDtoRequest
import systems.ajax.englishstudytelegrambot.service.WordService
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

@GrpcService
class GrpcWordService(
    val wordService: WordService
) : ReactorWordServiceGrpc.WordServiceImplBase() {

    override fun saveNewWord(request: Mono<SaveNewWordRequest>): Mono<SaveNewWordResponse> =
        request.flatMap {
            wordService.saveNewWord(it.libraryName, it.telegramUserId, it.createWordDtoRequest.toServiceDto())
        }.map {
            SaveNewWordResponse.newBuilder().apply {
                successBuilder.setWord(it.toWordResponse())
            }.build()
        }.onErrorResume {
            SaveNewWordResponse.newBuilder().apply {
                failureBuilder.setErrorMassage(it.message)
            }.build().toMono()
        }

    override fun saveNewWord(request: SaveNewWordRequest): Mono<SaveNewWordResponse> =
        saveNewWord(request.toMono())

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun updateWordTranslate(request: Mono<UpdateWordTranslateRequest>): Mono<UpdateWordTranslateResponse> =
        request.flatMap {
            wordService.updateWordTranslate(it.libraryName, it.telegramUserId, it.createWordDtoRequest.toServiceDto())
        }.map {
            UpdateWordTranslateResponse.newBuilder().apply {
                successBuilder.setWord(it.toWordResponse())
            }.build()
        }.onErrorResume {
            UpdateWordTranslateResponse.newBuilder().apply {
                failureBuilder.setErrorMassage(it.message)
            }.build().toMono()
        }

    override fun updateWordTranslate(request: UpdateWordTranslateRequest): Mono<UpdateWordTranslateResponse> =
        updateWordTranslate(request.toMono())

    override fun deleteWord(request: Mono<DeleteWordRequest>): Mono<DeleteWordResponse> =
        request.flatMap {
            wordService.deleteWord(it.libraryName, it.telegramUserId, it.wordSpelling)
        }.map {
            DeleteWordResponse.newBuilder().apply {
                successBuilder.setWord(it.toWordResponse())
            }.build()
        }.onErrorResume {
            DeleteWordResponse.newBuilder().apply {
                failureBuilder.setErrorMassage(it.message)
            }.build().toMono()
        }

    override fun deleteWord(request: DeleteWordRequest?): Mono<DeleteWordResponse> =
        deleteWord(request.toMono())

    override fun getFullInfoAboutWord(request: Mono<GetFullInfoAboutWordRequest>): Mono<GetFullInfoAboutWordResponse> =
        request.flatMap {
            wordService.getFullInfoAboutWord(it.libraryName, it.telegramUserId, it.wordSpelling)
        }.map {
            GetFullInfoAboutWordResponse.newBuilder().apply {
                successBuilder.setWord(it.toWordResponse())
            }.build()
        }.onErrorResume {
            GetFullInfoAboutWordResponse.newBuilder().apply {
                failureBuilder.setErrorMassage(it.message)
            }.build().toMono()
        }

    override fun getFullInfoAboutWord(request: GetFullInfoAboutWordRequest): Mono<GetFullInfoAboutWordResponse> =
        getFullInfoAboutWord(request.toMono())

    private fun CreateWordDtoRequest.toServiceDto(): ServiceCreateWordDtoRequest =
        ServiceCreateWordDtoRequest(spelling, translate)
}
