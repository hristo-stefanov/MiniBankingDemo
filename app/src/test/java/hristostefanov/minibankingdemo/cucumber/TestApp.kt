package hristostefanov.minibankingdemo.cucumber

import hristostefanov.minibankingdemo.cucumber.di.DaggerTestApplicationComponent
import hristostefanov.minibankingdemo.cucumber.di.TestApplicationComponent

object TestApp {
    private lateinit var _component: TestApplicationComponent
    val component = _component

    fun newComponent() {
        _component = DaggerTestApplicationComponent.create()
    }
}