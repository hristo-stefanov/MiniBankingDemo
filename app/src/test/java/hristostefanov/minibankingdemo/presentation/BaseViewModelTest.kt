package hristostefanov.minibankingdemo.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
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

    @ObsoleteCoroutinesApi
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()

        // workaround of a race-condition issue that randomly causes exceptions in tests
        sleep(2)

        mainThreadSurrogate.close()
    }
}