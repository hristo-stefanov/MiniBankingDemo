package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hristostefanov.starlingdemo.App

class ViewModelFactory(
    private val _application: App
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return when (modelClass) {
            AccessTokenViewModel::class.java -> {
                _application.newSession()
                _application.sessionComponent.getAccessTokenViewModel() as T
            }
            AccountsViewModel::class.java -> _application.sessionComponent.getAccountsViewModel() as T
            SavingsGoalsViewModel::class.java -> _application.sessionComponent.getSavingGoalsViewModel() as T
            CreateSavingsGoalViewModel::class.java -> _application.sessionComponent.getSavingsGoalViewModel() as T
            TransferConfirmationViewModel::class.java -> _application.sessionComponent.getTransferConfirmationViewModel() as T
            else -> throw IllegalArgumentException()
        }
    }
}