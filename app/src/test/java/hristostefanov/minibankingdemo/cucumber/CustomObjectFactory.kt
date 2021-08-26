package hristostefanov.minibankingdemo.cucumber

import io.cucumber.core.backend.ObjectFactory
import io.cucumber.core.exception.CucumberException

class CustomObjectFactory: ObjectFactory {

    private val instances = mutableMapOf<Class<*>,Any>()

    override fun addClass(glueClass: Class<*>?): Boolean {
        return true
    }

    override fun <T> getInstance(type: Class<T>): T? {
        var instance = type.cast(instances[type])
        if (instance == null) {
            instance = cacheNewInstance(type)
        }
        return instance
    }

    override fun start() {
    }

    override fun stop() {
    }

    private fun <T> cacheNewInstance(type: Class<T>): T {
        return try {
            val constructor = type.getConstructor()
            val instance = constructor.newInstance()
            instances[type] = instance!!
            instance
        } catch (e: NoSuchMethodException) {
            throw CucumberException(
                String.format(
                    "%s doesn't have an empty constructor. If you need dependency injection, put cucumber-picocontainer on the classpath",
                    type
                ), e
            )
        } catch (e: Exception) {
            throw CucumberException(String.format("Failed to instantiate %s", type), e)
        }
    }
}