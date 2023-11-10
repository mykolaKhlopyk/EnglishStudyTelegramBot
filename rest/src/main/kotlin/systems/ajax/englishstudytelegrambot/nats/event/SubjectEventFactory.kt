package systems.ajax.englishstudytelegrambot.nats.event

import systems.ajax.NatsSubject

object SubjectEventFactory {

    fun createUpdateWordEventSubject(spelling: String): String =
        buildString {
            append(NatsSubject.Event.GET_WORDS_UPDATES_BY_SPELLING_SUBJECT)
            append(spelling)
        }
}
