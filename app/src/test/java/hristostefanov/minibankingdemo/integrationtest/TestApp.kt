package hristostefanov.minibankingdemo.integrationtest

import hristostefanov.minibankingdemo.integrationtest.di.DaggerTestApplicationComponent
import hristostefanov.minibankingdemo.integrationtest.di.TestApplicationComponent

object TestApp {
    private var _component: TestApplicationComponent = DaggerTestApplicationComponent.create()
    val component = _component

    fun newComponent() {
        _component = DaggerTestApplicationComponent.create()
    }
}