package hristostefanov.starlingdemo.util

import dagger.Component
import hristostefanov.starlingdemo.presentation.*
import javax.inject.Singleton

@Singleton
@Component(modules = [ProvidingModule::class, BindingModule::class])
interface ApplicationComponent {
    fun getMainViewModel(): AccountsViewModel
    fun getSavingGoalsViewModel(): SavingsGoalsViewModel
    // TODO use consistent naming with other functions
    fun createSavingsGoalViewModel(): CreateSavingsGoalViewModel
    fun getTransferConfirmationViewModel(): TransferConfirmationViewModel
    fun getAccessTokenViewModel(): AccessTokenViewModel
}