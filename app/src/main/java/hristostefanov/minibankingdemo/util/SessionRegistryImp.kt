package hristostefanov.minibankingdemo.util

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRegistryImp @Inject constructor(
    private val sessionComponentFactory: SessionComponent.Factory
) : SessionRegistry {
    override var sessionComponent: SessionComponent = sessionComponentFactory.create()

    override fun newSession() {
        sessionComponent = sessionComponentFactory.create()
    }
}