package systems.ajax.englishstudytelegrambot.service

import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.dto.entity.LibraryDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.WordDtoResponse
import systems.ajax.englishstudytelegrambot.dto.entity.toDtoResponse
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.entity.Word
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository

interface LibraryService {

    fun createNewLibrary(nameOfNewLibrary: String, telegramUserId: String): LibraryDtoResponse

    fun deleteLibrary(nameOfLibraryForDeleting: String, telegramUserId: String): LibraryDtoResponse

    fun getAllWordsFromLibrary(libraryName: String, telegramUserId: String): List<WordDtoResponse>
}

@Service
class LibraryServiceImpl(
    val libraryRepository: LibraryRepository
) : LibraryService {

    override fun createNewLibrary(nameOfNewLibrary: String, telegramUserId: String): LibraryDtoResponse =
        libraryRepository.saveNewLibrary(nameOfNewLibrary, telegramUserId).toDtoResponse()

    override fun deleteLibrary(nameOfLibraryForDeleting: String, telegramUserId: String): LibraryDtoResponse {
        val libraryId =
            libraryRepository.getLibraryIdByLibraryNameAndTelegramUserId(nameOfLibraryForDeleting, telegramUserId)
        return libraryRepository.deleteLibrary(libraryId).toDtoResponse()
    }

    override fun getAllWordsFromLibrary(libraryName: String, telegramUserId: String): List<WordDtoResponse> {
        val libraryId = libraryRepository.getLibraryIdByLibraryNameAndTelegramUserId(libraryName, telegramUserId)
        return libraryRepository.getAllWordsFromLibrary(libraryId).map(Word::toDtoResponse)
    }
}
