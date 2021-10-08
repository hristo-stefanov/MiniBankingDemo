package hristostefanov.minibankingdemo.acceptancetest.technical

import hristostefanov.minibankingdemo.acceptancetest.technical.di.DaggerTestApplicationComponent
import hristostefanov.minibankingdemo.acceptancetest.technical.di.TestApplicationComponent
object TestApp {
    private var _component: TestApplicationComponent = DaggerTestApplicationComponent.create()
    val component = _component

    fun newComponent() {
        _component = DaggerTestApplicationComponent.create()
    }
}