package hristostefanov.minibankingdemo.acceptancetest.technical.di

import dagger.Component
import hristostefanov.minibankingdemo.acceptancetest.businessflow.*
import javax.inject.Singleton

@Singleton
@Component(modules = [TestApplicationModule::class])
interface TestApplicationComponent {
    fun inject(hooks: Hooks)
    fun inject(target: RoundUpSteps)
    fun inject(target: LoginSteps)
    fun inject(target: LogoutSteps)
    fun inject(target: EncourageUsersToSaveMoneySteps)
    fun inject(target: CommonPresentationSteps)
    fun inject(target: AutoLoginSteps)
}