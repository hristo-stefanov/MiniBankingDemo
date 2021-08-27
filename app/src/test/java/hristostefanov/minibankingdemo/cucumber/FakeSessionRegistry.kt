package hristostefanov.minibankingdemo.cucumber

import hristostefanov.minibankingdemo.cucumber.di.FakeSessionComponent
import hristostefanov.minibankingdemo.util.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class FakeSessionRegistry @Inject constructor(private val sessionComponentFactory: FakeSessionComponent.Factory) {
    var sessionComponent: FakeSessionComponent = sessionComponentFactory.create()

    fun newSession() {
        sessionComponent = sessionComponentFactory.create()
    }
}