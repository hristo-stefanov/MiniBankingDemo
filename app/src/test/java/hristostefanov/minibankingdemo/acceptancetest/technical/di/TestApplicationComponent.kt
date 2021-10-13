package hristostefanov.minibankingdemo.acceptancetest.technical.di

import dagger.Component
import hristostefanov.minibankingdemo.acceptancetest.businessflow.Hooks
import hristostefanov.minibankingdemo.acceptancetest.technical.TestSessionRegistry
import javax.inject.Singleton

@Singleton
@Component(modules = [TestApplicationModule::class])
interface TestApplicationComponent {
    fun getSessionRegistry(): TestSessionRegistry

    fun inject(hooks: Hooks)
}