package hristostefanov.minibankingdemo.util

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRegistryImp @Inject constructor(
    private val sessionComponentFactory: SessionComponent.Factory
) : SessionRegistry {

    override var sessionComponent: SessionComponent? = null

    override fun createSession(token: String) {
        sessionComponent = sessionComponentFactory.create(token)
    }

    override fun close() {
        sessionComponent = null
    }
}