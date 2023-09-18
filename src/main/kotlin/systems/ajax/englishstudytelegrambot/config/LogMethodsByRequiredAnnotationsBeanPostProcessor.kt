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

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        if (bean.javaClass.isAnnotationPresent(LogMethodsByRequiredAnnotations::class.java)) {
            LoggingInterceptor.requiredAnnotations =
                bean.javaClass.getAnnotation(LogMethodsByRequiredAnnotations::class.java).requiredAnnotations
            val proxyFactory = ProxyFactory(bean);
            proxyFactory.addAdvice(LoggingInterceptor);
            return proxyFactory.getProxy();
        }
        return bean;
    }

    object LoggingInterceptor : MethodInterceptor {
        var requiredAnnotations: Array<out KClass<out Any>> = arrayOf()
        override fun invoke(invocation: MethodInvocation): Any? {
            val methodAnnotations = invocation.method.annotations.map { it.annotationClass }
            if (methodAnnotations.any { requiredAnnotations.contains(it) }) {
                val start: Long = System.currentTimeMillis()
                val result: Any? = invocation.proceed()
                val finish: Long = System.currentTimeMillis()
                logFullInfoAboutMethod(invocation.method, finish - start, result)
                return result
            }
            return invocation.proceed()
        }

        private fun logFullInfoAboutMethod(method: Method, durationOfWorkInMs: Long, result: Any?) {
            log.info("method name = {}, duration of work = {} ms, result = {}", method.name, durationOfWorkInMs, result)
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
