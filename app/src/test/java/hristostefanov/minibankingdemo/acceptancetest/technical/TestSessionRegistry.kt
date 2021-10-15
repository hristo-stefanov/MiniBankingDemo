package hristostefanov.minibankingdemo.acceptancetest.technical

import hristostefanov.minibankingdemo.acceptancetest.technical.di.TestSessionComponent
import hristostefanov.minibankingdemo.util.ISessionRegistry
import hristostefanov.minibankingdemo.util.SessionComponent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestSessionRegistry @Inject constructor(private val sessionComponentFactory: TestSessionComponent.Factory) : ISessionRegistry {
    override var sessionComponent: SessionComponent = sessionComponentFactory.create()

    override fun newSession() {
        sessionComponent = sessionComponentFactory.create()
    }
}