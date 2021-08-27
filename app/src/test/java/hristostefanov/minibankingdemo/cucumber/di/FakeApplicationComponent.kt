package hristostefanov.minibankingdemo.cucumber.di

import dagger.Component
import hristostefanov.minibankingdemo.cucumber.FakeSessionRegistry
import hristostefanov.minibankingdemo.util.ApplicationScope

@ApplicationScope
@Component(modules = [FakeApplicationModule::class])
interface FakeApplicationComponent {
    fun getSessionRegistry(): FakeSessionRegistry
}