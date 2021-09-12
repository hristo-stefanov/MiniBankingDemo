package hristostefanov.minibankingdemo.cucumber.di

import dagger.Component
import hristostefanov.minibankingdemo.cucumber.TestSessionRegistry
import hristostefanov.minibankingdemo.util.ApplicationScope

@ApplicationScope
@Component(modules = [TestApplicationModule::class])
interface TestApplicationComponent {
    fun getSessionRegistry(): TestSessionRegistry
}