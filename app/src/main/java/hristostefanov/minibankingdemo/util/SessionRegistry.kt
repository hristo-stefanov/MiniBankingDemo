package hristostefanov.minibankingdemo.util

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRegistry @Inject constructor(
    private val sessionComponentFactory: SessionComponent.Factory
) : ISessionRegistry {
    override var sessionComponent: SessionComponent = sessionComponentFactory.create()

    override fun newSession() {
        sessionComponent = sessionComponentFactory.create()
    }
}