package systems.ajax.englishstudytelegrambot.config

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.slf4j.LoggerFactory
import org.springframework.aop.framework.ProxyFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import systems.ajax.englishstudytelegrambot.annotation.LogMethodsByRequiredAnnotations
import java.lang.reflect.Method
import kotlin.reflect.KClass


@Component
class LogMethodsByRequiredAnnotationsBeanPostProcessor : BeanPostProcessor {

    private val mapBeanNameBeanClassAndAnnotatedMethods: MutableMap<String, Pair<Class<*>, List<Method>>> =
        mutableMapOf()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val beanClass = bean.javaClass
        if (beanClass.isAnnotationPresent(LogMethodsByRequiredAnnotations::class.java)) {
            mapBeanNameBeanClassAndAnnotatedMethods[beanName] =
                beanClass to findMethodsWithRequiredAnnotations(beanClass)
        }
        return bean
    }

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

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        mapBeanNameBeanClassAndAnnotatedMethods[beanName]?.run {
            val proxyFactory = ProxyFactory(bean)
            putAdviceInProxyFactory(proxyFactory, beanName)
            return proxyFactory.getProxy()
        }
        return bean
    }

    private fun putAdviceInProxyFactory(proxyFactory: ProxyFactory, beanName: String) {
        proxyFactory.addAdvice(
            object : MethodInterceptor {
                override fun invoke(invocation: MethodInvocation): Any? {
                    if (mapBeanNameBeanClassAndAnnotatedMethods[beanName]!!.second.containsRequiredMethod(invocation.method)) {
                        return invocation.decorateAndReturnResult()
                    }
                    return invocation.proceed()
                }

                private fun List<Method>.containsRequiredMethod(method: Method): Boolean =
                    any { it.name == method.name && it.parameterTypes.contentEquals(method.parameterTypes) }

                private fun MethodInvocation.decorateAndReturnResult(): Any? {
                    val startTime: Long = System.currentTimeMillis()
                    val resultOfMethod: Any? = proceed()
                    val finishTime: Long = System.currentTimeMillis()
                    val durationOfFunctionWork: Long = finishTime - startTime
                    logFullInfoAboutMethod(method, durationOfFunctionWork, resultOfMethod)
                    return resultOfMethod
                }

                private fun logFullInfoAboutMethod(method: Method, durationOfWorkInMs: Long, result: Any?) {
                    log.info(
                        "method name = {}, duration of work = {} ms, result = {}",
                        method.name,
                        durationOfWorkInMs,
                        result
                    )
                }
            })
    }

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
