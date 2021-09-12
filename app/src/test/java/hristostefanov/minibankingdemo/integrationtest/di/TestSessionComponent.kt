package hristostefanov.minibankingdemo.integrationtest.di

import dagger.Subcomponent
import hristostefanov.minibankingdemo.integrationtest.steps.RoundUpSteps
import hristostefanov.minibankingdemo.util.SessionScope

@SessionScope
@Subcomponent(modules = [TestSessionModule::class])
interface TestSessionComponent {
    // NOTE: required even if trivial
    @Subcomponent.Factory
    interface Factory {
        fun create(): TestSessionComponent
    }

    fun inject(target: RoundUpSteps)
}