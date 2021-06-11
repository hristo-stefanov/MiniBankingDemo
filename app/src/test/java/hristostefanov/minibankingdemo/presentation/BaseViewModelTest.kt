package hristostefanov.minibankingdemo.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.lang.Thread.sleep

open class BaseViewModelTest {
    // NOTE: needed for proper testing of Architecture Components -
    // makes background tasks execute synchronously.
    // More importantly, provides TaskExecutor#isMainThread implementation which always return `true`
    // thus avoiding exceptions in LiveData's observe* methods.
    @get:Rule
    val rule = InstantTaskExecutorRule()


    // NOTE: newSingleThreadContext() is marked with @ObsoleteCoroutinesApi, so
    // from the docs, it looks like TestCoroutineDispatcher is the preferred means.
    // See https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/
    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()

        // TODO still needed with TestCoroutineDispatcher?
        // To get rid of the exceptions in the output, might need to run the tests as androidTest
        // and possibly with @UiThreadTest annotation.

        // workaround of a race-condition issue that randomly causes exceptions in tests
        sleep(10)

        testDispatcher.cleanupTestCoroutines()
    }
}