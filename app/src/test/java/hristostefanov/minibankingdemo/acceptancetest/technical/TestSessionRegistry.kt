package hristostefanov.minibankingdemo.acceptancetest.technical

import hristostefanov.minibankingdemo.acceptancetest.technical.di.TestSessionComponent
import hristostefanov.minibankingdemo.util.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class TestSessionRegistry @Inject constructor(private val sessionComponentFactory: TestSessionComponent.Factory) {
    var sessionComponent: TestSessionComponent = sessionComponentFactory.create()

    fun newSession() {
        sessionComponent = sessionComponentFactory.create()
    }
}