package hristostefanov.minibankingdemo.cucumber

import hristostefanov.minibankingdemo.cucumber.di.TestSessionComponent
import hristostefanov.minibankingdemo.util.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class TestSessionRegistry @Inject constructor(private val sessionComponentFactory: TestSessionComponent.Factory) {
    var sessionComponent: TestSessionComponent = sessionComponentFactory.create()

    fun newSession() {
        sessionComponent = sessionComponentFactory.create()
    }
}