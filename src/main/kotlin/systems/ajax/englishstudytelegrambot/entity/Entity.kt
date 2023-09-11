package systems.ajax.englishstudytelegrambot.entity

class Word(val spelling: String, val translate: String, val additionalInfoAboutWord: AdditionalInfoAboutWord)

data class AdditionalInfoAboutWord(
    val linkToAudio: String,
    val definition: String,
    val example: String,
    val pronunciationOfWord: String)

class Library(val name: String, val words: List<Word>)
