package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hristostefanov.starlingdemo.util.ApplicationComponent

class ViewModelFactory constructor(private val applicationComponent: ApplicationComponent): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return when (modelClass) {
            AccountsViewModel::class.java -> applicationComponent.getAccountsViewModel() as T
            SavingsGoalsViewModel::class.java -> applicationComponent.getSavingGoalsViewModel() as T
            CreateSavingsGoalViewModel::class.java -> applicationComponent.getSavingsGoalViewModel() as T
            TransferConfirmationViewModel::class.java -> applicationComponent.getTransferConfirmationViewModel() as T
            AccessTokenViewModel::class.java -> applicationComponent.getAccessTokenViewModel() as T
            else -> throw IllegalArgumentException()
        }
    }
}