package hristostefanov.minibankingdemo.util

import hristostefanov.minibankingdemo.usecase.output.Summary
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Session @Inject constructor(
    private val tokenComponentFactory: TokenComponent.Factory,
) {
    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private var _tokenComponent: TokenComponent? = null

    val tokenComponent: TokenComponent?
        get() = _tokenComponent

    // TODO this can crash
    val requireTokenComponent: TokenComponent
        get() = tokenComponent ?: throw IllegalStateException("No login session")

    fun setToken(token: String, tokenType: String) {
        _tokenComponent = tokenComponentFactory.create(token, tokenType)
        updateState { it.copy(hasToken = true) }
    }

    fun resetSession() {
        // TODO: Prevent new operations on TokenComponent
        _tokenComponent = null

        _state.value = State()
    }

    fun updateState(transform: (State) -> State) {
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