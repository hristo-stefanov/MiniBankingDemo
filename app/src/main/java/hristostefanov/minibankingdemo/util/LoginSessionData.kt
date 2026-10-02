package hristostefanov.minibankingdemo.util

import hristostefanov.minibankingdemo.usecase.output.Summary
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginSessionData @Inject constructor() {
    val summary = MutableStateFlow<Summary?>(null)

    var selectedSavingsGoalId: String? = null

    val selectedAccountIdFlow = MutableStateFlow<String?>(null)

    val selectedAccount: Summary.Item?
        get() = summary.value?.items?.find { it.accountId == selectedAccountIdFlow.value }

    fun clear() {
        summary.value = null
        selectedAccountIdFlow.value = null
    }
}