package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import io.cucumber.java.After
import io.cucumber.java.Before
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class Hooks {
    @Inject
    internal lateinit var testDispatcher: TestCoroutineDispatcher

    init {
        TestApp.component.inject(this)
    }

    // this hook recreates the test app component so it must run before any other hook
    // that might inject from it
    @Before(order = 0)
    fun beforeEachScenario() {
        TestApp.newComponent()
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun afterEachScenario() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}