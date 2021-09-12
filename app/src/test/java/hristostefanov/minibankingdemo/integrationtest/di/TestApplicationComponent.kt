package hristostefanov.minibankingdemo.integrationtest.di

import dagger.Component
import hristostefanov.minibankingdemo.integrationtest.TestSessionRegistry
import hristostefanov.minibankingdemo.util.ApplicationScope

@ApplicationScope
@Component(modules = [TestApplicationModule::class])
interface TestApplicationComponent {
    fun getSessionRegistry(): TestSessionRegistry
}