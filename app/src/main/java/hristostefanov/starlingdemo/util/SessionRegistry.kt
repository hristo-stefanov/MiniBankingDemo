package hristostefanov.starlingdemo.util

import javax.inject.Inject

@ApplicationScope
class SessionRegistry @Inject constructor(private val sessionComponentFactory: SessionComponent.Factory) {
    var sessionComponent: SessionComponent = sessionComponentFactory.create()

    fun newSession() {
        sessionComponent = sessionComponentFactory.create()
    }
}