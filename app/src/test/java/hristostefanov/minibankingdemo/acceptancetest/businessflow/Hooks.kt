package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.MainDispatcherRule
import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import io.cucumber.java.After
import io.cucumber.java.Before
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import javax.inject.Inject

// TODO rename to CommonScenarioHooks or SharedHooks or something
//  cause we have such hooks in the *Steps classes too
@OptIn(ExperimentalCoroutinesApi::class)
class Hooks {

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