package systems.ajax.englishstudytelegrambot.entity

data class Word(val spelling: String, val translate: String, val additionalInfoAboutWord: AdditionalInfoAboutWord)

data class AdditionalInfoAboutWord(
    val linkToAudio: String,
    val definition: String,
    val example: String,
    val pronunciationOfWord: String
)

data class Library(val name: String, val words: List<Word>)
