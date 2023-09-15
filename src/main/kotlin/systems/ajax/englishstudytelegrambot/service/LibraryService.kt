package systems.ajax.englishstudytelegrambot.service

import org.springframework.stereotype.Service
import systems.ajax.englishstudytelegrambot.entity.Library
import systems.ajax.englishstudytelegrambot.repository.LibraryRepository

interface LibraryService {

    fun createNewLibrary(nameOfNewLibrary: String, ownerId: String): Library
}

@Service
class LibraryServiceImpl(val libraryRepository: LibraryRepository) : LibraryService {

    override fun createNewLibrary(nameOfNewLibrary: String, ownerId: String): Library =
        libraryRepository.save(Library(nameOfNewLibrary, ownerId))

}
