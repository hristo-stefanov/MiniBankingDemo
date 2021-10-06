package hristostefanov.minibankingdemo.acceptancetest.technical

import hristostefanov.minibankingdemo.acceptancetest.technical.di.DaggerTestApplicationComponent
import hristostefanov.minibankingdemo.acceptancetest.technical.di.TestApplicationComponent
// TODO see https://developer.android.com/training/dependency-injection/dagger-android#dagger-end-to-end-tests
object TestApp {
    private var _component: TestApplicationComponent = DaggerTestApplicationComponent.create()
    val component = _component

    fun newComponent() {
        _component = DaggerTestApplicationComponent.create()
    }
}