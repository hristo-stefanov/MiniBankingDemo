package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import io.cucumber.java.After
import io.cucumber.java.Before
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

/**
 * Contains hooks common for all scenarios. Steps files contain scenario specific hooks.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CommonHooks {

    // This hook recreates the test app component so it must run before any other hooks
    // that might inject from it
    @Before(order = 0)
    fun beforeEachScenario() {
        TestApp.newComponent()

        // Same technique as in [MainDispatcherRule]
        // runTest will reuse the scheduler of this dispatcher, given the main dispatcher
        // is set first
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun afterEachScenario() {
        Dispatchers.resetMain()
    }
}