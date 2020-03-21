package hristostefanov.starlingdemo.util

import dagger.Component
import hristostefanov.starlingdemo.presentation.*
import javax.inject.Singleton

@Singleton
@Component(modules = [ProvidingModule::class, BindingModule::class])
interface ApplicationComponent {
    fun getAccountsViewModel(): AccountsViewModel
    fun getSavingGoalsViewModel(): SavingsGoalsViewModel
    fun getSavingsGoalViewModel(): CreateSavingsGoalViewModel
    fun getTransferConfirmationViewModel(): TransferConfirmationViewModel
    fun getAccessTokenViewModel(): AccessTokenViewModel
}