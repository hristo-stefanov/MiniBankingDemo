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

    fun inject(target: AccessTokenViewModel)
    fun inject(target: AccountsViewModel)
    fun inject(target: TransferConfirmationViewModel)
    fun inject(target: SavingsGoalsViewModel)

    fun getCreateSavingsGoalViewModel(): CreateSavingsGoalViewModel
}