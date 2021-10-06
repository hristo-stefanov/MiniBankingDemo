package hristostefanov.minibankingdemo.acceptancetest.technical.di

import dagger.Component
import hristostefanov.minibankingdemo.acceptancetest.technical.TestSessionRegistry
import hristostefanov.minibankingdemo.util.ApplicationScope

@ApplicationScope
@Component(modules = [TestApplicationModule::class])
interface TestApplicationComponent {
    fun getSessionRegistry(): TestSessionRegistry
}