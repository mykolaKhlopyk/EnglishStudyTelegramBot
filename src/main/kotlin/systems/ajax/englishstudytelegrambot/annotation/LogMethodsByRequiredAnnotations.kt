package systems.ajax.englishstudytelegrambot.annotation

import java.lang.reflect.Method
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class LogMethodsByRequiredAnnotations(
    vararg val requiredAnnotations: KClass<out Any>
)
