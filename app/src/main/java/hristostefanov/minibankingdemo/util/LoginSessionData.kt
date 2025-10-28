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
    var selectedAccount: Summary.Item? = null

    // TODO we need a better way to handle logged out state
    fun clear() {
        summary.value = null
        savingsGoalId = ""
        savingsGoalName = ""
        selectedAccount = null
    }
}