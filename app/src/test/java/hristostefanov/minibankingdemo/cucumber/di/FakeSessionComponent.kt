package hristostefanov.minibankingdemo.cucumber.di

import dagger.Subcomponent
import hristostefanov.minibankingdemo.cucumber.StepDefinitions
import hristostefanov.minibankingdemo.util.SessionScope

@SessionScope
@Subcomponent(modules = [FakeSessionModule::class])
interface FakeSessionComponent {
    // NOTE: required even if trivial
    @Subcomponent.Factory
    interface Factory {
        fun create(): FakeSessionComponent
    }

    fun inject(target: StepDefinitions)
}