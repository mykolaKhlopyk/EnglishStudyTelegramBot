package systems.ajax.englishstudytelegrambot.annotation

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
annotation class LogMethodsByRequiredAnnotations(
    vararg val requiredAnnotations: KClass<out Any>
)
