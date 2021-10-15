package hristostefanov.minibankingdemo.acceptancetest.technical.di

import dagger.Subcomponent
import hristostefanov.minibankingdemo.acceptancetest.businessflow.EncourageUsersToSaveMoneySteps
import hristostefanov.minibankingdemo.acceptancetest.businessflow.RoundUpSteps
import hristostefanov.minibankingdemo.util.SessionComponent
import hristostefanov.minibankingdemo.util.SessionScope

@SessionScope
@Subcomponent(modules = [TestSessionModule::class])
interface TestSessionComponent: SessionComponent {
    // NOTE: required even if trivial
    @Subcomponent.Factory
    interface Factory {
        fun create(): TestSessionComponent
    }
}