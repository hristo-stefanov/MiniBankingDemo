package hristostefanov.minibankingdemo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.Mockito

// There are some problems with using Mockito with Kotlin as described:
// https://discuss.kotlinlang.org/t/how-to-use-mockito-with-kotlin/324
// https://stackoverflow.com/questions/30305217/is-it-possible-to-use-mockito-in-kotlin
// hence the workaround functions:

fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

fun <T> any(): T {
    return Mockito.any<T>()
}

fun <T> eq(t: T): T = Mockito.eq<T>(t)
fun <T> uninitialized(): T = null as T

// Originating from: https://medium.com/androiddevelopers/easy-coroutines-in-android-viewmodelscope-25bffb605471
// NOTE: "we call the runBlockingTest method inside the TestCoroutineDispatcher that the
// rule creates. Since that Dispatcher overrides Dispatchers.Main, MainViewModel will run the
// coroutine on that Dispatcher too. Calling runBlockingTest will make that coroutine to execute
// synchronously in the test."

@ExperimentalCoroutinesApi
class CoroutinesTestRule(
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}