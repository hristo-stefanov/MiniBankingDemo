package hristostefanov.minibankingdemo.cucumber.di

import dagger.Subcomponent
import features.RoundUpSteps
import hristostefanov.minibankingdemo.util.SessionScope

@SessionScope
@Subcomponent(modules = [FakeSessionModule::class])
interface FakeSessionComponent {
    // NOTE: required even if trivial
    @Subcomponent.Factory
    interface Factory {
        fun create(): FakeSessionComponent
    }

    fun inject(target: RoundUpSteps)
}