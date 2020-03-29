package hristostefanov.starlingdemo.util

import dagger.Subcomponent
import hristostefanov.starlingdemo.presentation.*

@SessionScope
@Subcomponent(modules = [SessionModule::class])
interface SessionComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SessionComponent
    }

    fun inject(vm: AccessTokenViewModel)
    fun inject(vm: AccountsViewModel)
    fun inject(vm: TransferConfirmationViewModel)
    fun inject(vm: CreateSavingsGoalViewModel)
    fun inject(vm: SavingsGoalsViewModel)
}