package systems.ajax.infrastructure.grpc

import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.application.ports.input.WordServiceIn
import systems.ajax.infrastructure.nats.mapper.toProto
import systems.ajax.service.CreateWordDtoRequest as ProtoCreateWordDtoRequest
import systems.ajax.service.DeleteWordRequest
import systems.ajax.service.DeleteWordResponse
import systems.ajax.service.GetFullInfoAboutWordRequest
import systems.ajax.service.GetFullInfoAboutWordResponse
import systems.ajax.service.ReactorWordServiceGrpc
import systems.ajax.service.SaveNewWordRequest
import systems.ajax.service.SaveNewWordResponse
import systems.ajax.service.UpdateWordTranslateRequest
import systems.ajax.service.UpdateWordTranslateResponse
import systems.ajax.infrastructure.rest.dto.request.CreateWordDtoRequest

@GrpcService
class GrpcWordService(
    val wordService: WordServiceIn,
) : ReactorWordServiceGrpc.WordServiceImplBase() {

    override fun saveNewWord(request: Mono<SaveNewWordRequest>): Mono<SaveNewWordResponse> =
        request.flatMap {
            wordService.saveNewWord(it.libraryName, it.telegramUserId, it.createWordDtoRequest.toServiceDto())
        }.map {
            SaveNewWordResponse.newBuilder().apply {
                successBuilder.setWord(it.toProto())
            }.build()
        }.onErrorResume {
            SaveNewWordResponse.newBuilder().apply {
                failureBuilder.setErrorMassage(it.message)
            }.build().toMono()
        }

    override fun updateWordTranslate(request: Mono<UpdateWordTranslateRequest>): Mono<UpdateWordTranslateResponse> =
        request.flatMap {
            wordService.updateWordTranslate(it.libraryName, it.telegramUserId, it.createWordDtoRequest.toServiceDto())
        }.map {
            UpdateWordTranslateResponse.newBuilder().apply {
                successBuilder.setWord(it.toProto())
            }.build()
        }.onErrorResume {
            UpdateWordTranslateResponse.newBuilder().apply {
                failureBuilder.setErrorMassage(it.message)
            }.build().toMono()
        }

    override fun deleteWord(request: Mono<DeleteWordRequest>): Mono<DeleteWordResponse> =
        request.flatMap {
            wordService.deleteWord(it.libraryName, it.telegramUserId, it.wordSpelling)
        }.map {
            DeleteWordResponse.newBuilder().apply {
                successBuilder.setWord(it.toProto())
            }.build()
        }.onErrorResume {
            DeleteWordResponse.newBuilder().apply {
                failureBuilder.setErrorMassage(it.message)
            }.build().toMono()
        }

    override fun getFullInfoAboutWord(request: Mono<GetFullInfoAboutWordRequest>): Mono<GetFullInfoAboutWordResponse> =
        request.flatMap {
            wordService.getFullInfoAboutWord(it.libraryName, it.telegramUserId, it.wordSpelling)
        }.map {
            GetFullInfoAboutWordResponse.newBuilder().apply {
                successBuilder.setWord(it.toProto())
            }.build()
        }.onErrorResume {
            GetFullInfoAboutWordResponse.newBuilder().apply {
                failureBuilder.setErrorMassage(it.message)
            }.build().toMono()
        }

    private fun ProtoCreateWordDtoRequest.toServiceDto(): CreateWordDtoRequest =
        CreateWordDtoRequest(spelling, translate)
}
