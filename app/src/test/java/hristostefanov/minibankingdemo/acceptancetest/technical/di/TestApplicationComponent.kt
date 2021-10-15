package hristostefanov.minibankingdemo.acceptancetest.technical.di

import dagger.Component
import hristostefanov.minibankingdemo.acceptancetest.businessflow.EncourageUsersToSaveMoneySteps
import hristostefanov.minibankingdemo.acceptancetest.businessflow.Hooks
import hristostefanov.minibankingdemo.acceptancetest.businessflow.RoundUpSteps
import hristostefanov.minibankingdemo.util.SessionRegistry
import javax.inject.Singleton

@Singleton
@Component(modules = [TestApplicationModule::class])
interface TestApplicationComponent {
    fun getSessionRegistry(): SessionRegistry

    fun inject(hooks: Hooks)
    fun inject(target: RoundUpSteps)
    fun inject(target: EncourageUsersToSaveMoneySteps)
}