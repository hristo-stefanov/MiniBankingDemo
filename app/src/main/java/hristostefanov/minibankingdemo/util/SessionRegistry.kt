package hristostefanov.minibankingdemo.util

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRegistry @Inject constructor(
    // TODO try with Provider
    // see https://medium.com/androiddevelopers/hilt-adding-components-to-the-hierarchy-96f207d6d92d
    private val sessionComponentFactory: SessionComponent.Factory
) {
    var sessionComponent: SessionComponent = sessionComponentFactory.create()

    fun newSession() {
        sessionComponent = sessionComponentFactory.create()
    }
}