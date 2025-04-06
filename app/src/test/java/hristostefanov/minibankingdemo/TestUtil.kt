package hristostefanov.minibankingdemo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
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

/**
 * This rule sets a [TestDispatcher] as a [Dispatchers.Main] for
 * [androidx.lifecycle.viewModelScope] and others that are hardcoded to
 * use [Dispatchers.Main]. This makes injecting a main dispatcher into ViewModels unnecessary.
 *
 * Based on https://developer.android.com/kotlin/coroutines/test#setting-main-dispatcher
 * > some APIs such as viewModelScope use a hardcoded Main dispatcher under the hood.
 *
 * > If the Main dispatcher has been replaced with a TestDispatcher, any newly-created
 * > TestDispatchers will automatically use the scheduler from the Main dispatcher, including
 * > the StandardTestDispatcher created by runTest if no other dispatcher is passed to it.
 *
 * > This makes it easier to ensure that there is only a single scheduler in use during the test.
 * > For this to work, make sure to create all other TestDispatcher instances after
 * > calling Dispatchers.setMain.
 *
 * > This rule implementation uses an UnconfinedTestDispatcher by default, but a
 * > StandardTestDispatcher can be passed in as a parameter if the Main dispatcher shouldn’t
 * > execute eagerly in a given test class.
 */
// TODO rename to MainDispatcherRule
class CoroutinesTestRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}