package hristostefanov.minibankingdemo.util

import hristostefanov.minibankingdemo.usecase.output.Summary
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginSessionData @Inject constructor() {
    val summary = MutableStateFlow<Summary?>(null)
    lateinit var savingsGoalId: String
    lateinit var savingsGoalName: String
    val selectedAccount: Summary.Item?
        get() = summary.value?.items?.find { it.accountId == selectedAccountIdFlow.value }
    val selectedAccountIdFlow = MutableStateFlow<String?>(null)

    fun clear() {
        summary.value = null
        selectedAccountIdFlow.value = null
    }
}