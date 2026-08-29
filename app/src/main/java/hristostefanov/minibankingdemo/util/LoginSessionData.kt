package hristostefanov.minibankingdemo.util

import hristostefanov.minibankingdemo.usecase.output.Summary
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@LoginSessionScope
class LoginSessionData @Inject constructor() {
    val summary = MutableStateFlow<Summary?>(null)
    lateinit var savingsGoalId: String
    lateinit var savingsGoalName: String
    val selectedAccount: Summary.Item?
        get() = summary.value?.items?.find { it.accountId == selectedAccountIdFlow.value }
    val selectedAccountIdFlow = MutableStateFlow<String?>(null)
}