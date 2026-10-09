package hristostefanov.minibankingdemo.util

import hristostefanov.minibankingdemo.usecase.output.Summary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Session @Inject constructor() {
    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun clear() {
        _state.value = State()
    }

    fun update(transform: (State) -> State) {
        _state.update(transform)
    }

    data class State(
        val summary: Summary? = null,
        val selectedAccountId: String? = null,
        val selectedSavingsGoalId: String? = null,
        val hasToken: Boolean = false
    ) {
        val selectedAccount: Summary.Item?
            get() = summary?.items?.find { it.accountId == selectedAccountId } ?: summary?.items?.getOrNull(0)
    }
}