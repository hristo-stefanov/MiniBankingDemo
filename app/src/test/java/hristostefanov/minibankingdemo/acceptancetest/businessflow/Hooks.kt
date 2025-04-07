package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.MainDispatcherRule
import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import io.cucumber.java.After
import io.cucumber.java.Before
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import javax.inject.Inject

// TODO rename to CommonScenarioHooks or SharedHooks or something
//  cause we have such hooks in the *Steps classes too
class Hooks {

    // this hook recreates the test app component so it must run before any other hook
    // that might inject from it
    @Before(order = 0)
    fun beforeEachScenario() {
        TestApp.newComponent()
        // Same as in MainDispatcherRule
        @OptIn(ExperimentalCoroutinesApi::class)
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun afterEachScenario() {
        Dispatchers.resetMain()
    }
}