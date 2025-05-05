package hristostefanov.minibankingdemo.acceptancetest.technical.di

import dagger.Component
import hristostefanov.minibankingdemo.acceptancetest.businessflow.*
import javax.inject.Singleton

@Singleton
@Component(modules = [TestApplicationModule::class])
interface TestApplicationComponent {
    fun inject(target: CalculationsSteps)
    fun inject(target: LoginSteps)
    fun inject(target: LogoutSteps)
    fun inject(target: SaveRoundUps)
    fun inject(target: CommonPresentationSteps)
    fun inject(target: AutoLoginSteps)
}