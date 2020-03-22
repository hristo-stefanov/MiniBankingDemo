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
                _application.session.getAccessTokenViewModel() as T
            }
            AccountsViewModel::class.java -> _application.session.getAccountsViewModel() as T
            SavingsGoalsViewModel::class.java -> _application.session.getSavingGoalsViewModel() as T
            CreateSavingsGoalViewModel::class.java -> _application.session.getSavingsGoalViewModel() as T
            TransferConfirmationViewModel::class.java -> _application.session.getTransferConfirmationViewModel() as T
            else -> throw IllegalArgumentException()
        }
    }
}