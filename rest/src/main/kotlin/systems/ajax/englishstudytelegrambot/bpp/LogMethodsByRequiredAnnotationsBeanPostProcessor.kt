package systems.ajax.englishstudytelegrambot.bpp

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.cglib.proxy.Enhancer
import org.springframework.cglib.proxy.MethodProxy
import org.springframework.stereotype.Component
import systems.ajax.englishstudytelegrambot.annotation.LogMethodsByRequiredAnnotations
import java.lang.reflect.Method
import kotlin.reflect.KClass
import org.springframework.cglib.proxy.MethodInterceptor;


@Component
class LogMethodsByRequiredAnnotationsBeanPostProcessor : BeanPostProcessor {

    private val mapBeanNameBeanClassAndAnnotatedMethods: MutableMap<String, Pair<Class<*>, List<Method>>> =
        mutableMapOf()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        val beanClass = bean.javaClass
        if (beanClass.isAnnotationPresent(LogMethodsByRequiredAnnotations::class.java)) {
            mapBeanNameBeanClassAndAnnotatedMethods[beanName] =
                beanClass to findMethodsWithRequiredAnnotations(beanClass)
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any =
        mapBeanNameBeanClassAndAnnotatedMethods.get(beanName)?.let { enhanceBean(bean, beanName) } ?: bean

    private fun findMethodsWithRequiredAnnotations(beanClass: Class<*>): List<Method> {
        val requiredAnnotations: Array<out KClass<out Any>> =
            beanClass.getAnnotation(LogMethodsByRequiredAnnotations::class.java).requiredAnnotations
        return beanClass
            .getDeclaredMethods()
            .asSequence()
            .filter { method ->
                method.annotations
                    .map { it.annotationClass }
                    .any { requiredAnnotations.contains(it) }
            }
            .toList()
    }

    private fun enhanceBean(bean: Any, beanName: String): Any {
        val enhancer = Enhancer()
        enhancer.setSuperclass(mapBeanNameBeanClassAndAnnotatedMethods[beanName]!!.first)
        putCallbackInEnhancer(enhancer, beanName, bean)
        return enhancer.create()
    }

    @Suppress("SpreadOperator")
    private fun putCallbackInEnhancer(enhancer: Enhancer, beanName: String, bean: Any) {
        enhancer.setCallback(
            object : MethodInterceptor {

                override fun intercept(obj: Any?, method: Method?, args: Array<out Any>?, proxy: MethodProxy?): Any {
                    if (mapBeanNameBeanClassAndAnnotatedMethods[beanName]!!.second.containsRequiredMethod(method!!)) {
                        return method.decorateAndReturnResult(bean, args)
                    }
                    return method.invoke(bean, *args.orEmpty())
                }

                private fun List<Method>.containsRequiredMethod(method: Method): Boolean =
                    any { it.name == method.name && it.parameterTypes.contentEquals(method.parameterTypes) }

                private fun Method.decorateAndReturnResult(obj: Any?, args: Array<out Any>?): Any {
                    val startTime: Long = System.currentTimeMillis()
                    val resultOfMethod: Any = invoke(obj, *args.orEmpty())
                    val finishTime: Long = System.currentTimeMillis()
                    val durationOfFunctionWork: Long = finishTime - startTime
                    logFullInfoAboutMethod(durationOfFunctionWork, resultOfMethod)
                    return resultOfMethod
                }

                private fun Method.logFullInfoAboutMethod(durationOfWorkInMs: Long, result: Any?) {
                    log.info(
                        "methods name = {}, duration of work = {} ms, result = {}",
                        name,
                        durationOfWorkInMs,
                        result
                    )
                }
            }
        )
    }

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
