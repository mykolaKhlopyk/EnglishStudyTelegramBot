package systems.ajax.infrastructure.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class LogMethodsByRequiredAnnotations(
    vararg val requiredAnnotations: KClass<out Any>
)
