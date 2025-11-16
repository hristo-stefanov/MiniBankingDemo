package hristostefanov.minibankingdemo.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class LoginSessionRegistryImp @Inject constructor(
    private val loginSessionComponentFactory: LoginSessionComponent.Factory
) : LoginSessionRegistry {

    private val _component = MutableStateFlow<LoginSessionComponent?>(null)

    override val component: LoginSessionComponent?
        get() = _component.value

    override val componentFlow: StateFlow<LoginSessionComponent?> = _component.asStateFlow()

    override fun createSession(token: String, tokenType: String) {
        _component.value = loginSessionComponentFactory.create(token, tokenType)
    }

    override fun closeSession() {
        _component.value = null
    }
}