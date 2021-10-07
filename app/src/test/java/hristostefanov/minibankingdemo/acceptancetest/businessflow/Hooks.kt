package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.acceptancetest.technical.TestApp
import io.cucumber.java.After
import io.cucumber.java.Before
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

class Hooks {
    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
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