package hristostefanov.minibankingdemo.acceptancetest.technical

import hristostefanov.minibankingdemo.acceptancetest.technical.di.TestSessionComponent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestSessionRegistry @Inject constructor(private val sessionComponentFactory: TestSessionComponent.Factory) {
    var sessionComponent: TestSessionComponent = sessionComponentFactory.create()

    fun newSession() {
        sessionComponent = sessionComponentFactory.create()
    }
}