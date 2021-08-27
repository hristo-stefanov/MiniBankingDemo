package hristostefanov.minibankingdemo.cucumber

import hristostefanov.minibankingdemo.cucumber.di.DaggerFakeApplicationComponent
import io.cucumber.core.backend.ObjectFactory
import io.cucumber.core.exception.CucumberException

class CustomObjectFactory: ObjectFactory {

    private val instances = mutableMapOf<Class<*>,Any>()

    override fun addClass(glueClass: Class<*>?): Boolean {
        return true
    }

    // Copied from DefaultJavaObjectFactory
    override fun <T> getInstance(type: Class<T>): T? {
        var instance = type.cast(instances[type])
        if (instance == null) {
            instance = cacheNewInstance(type)
        }
        return instance
    }

    override fun start() {
        TestComponentRegistry.applicationComponent = DaggerFakeApplicationComponent.create()
    }

    override fun stop() {
    }

    // Copied from DefaultJavaObjectFactory
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