package hristostefanov.minibankingdemo.util

import hristostefanov.minibankingdemo.usecase.output.Summary
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@LoginSessionScope
class LoginSessionData @Inject constructor() {
    val summary = MutableStateFlow<Summary?>(null)
    lateinit var savingsGoalId: String
    lateinit var savingsGoalName: String
    lateinit var selectedAccount: Summary.Item
}