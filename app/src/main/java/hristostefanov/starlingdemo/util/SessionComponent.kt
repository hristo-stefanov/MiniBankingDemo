package hristostefanov.starlingdemo.util

import dagger.Subcomponent
import hristostefanov.starlingdemo.presentation.*

@SessionScope
@Subcomponent(modules = [ProvidingModule::class, BindingModule::class])
interface SessionComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SessionComponent
    }

    fun getAccessTokenViewModel(): AccessTokenViewModel
    fun getAccountsViewModel(): AccountsViewModel
    fun getSavingGoalsViewModel(): SavingsGoalsViewModel
    fun getSavingsGoalViewModel(): CreateSavingsGoalViewModel
    fun getTransferConfirmationViewModel(): TransferConfirmationViewModel
}