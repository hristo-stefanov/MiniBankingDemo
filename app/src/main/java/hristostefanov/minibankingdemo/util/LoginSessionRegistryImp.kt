package hristostefanov.minibankingdemo.util

import javax.inject.Inject

class LoginSessionRegistryImp @Inject constructor(
    private val loginSessionComponentFactory: LoginSessionComponent.Factory,
    private val session: Session
) : LoginSessionRegistry {

    private var _component: LoginSessionComponent? = null

    override val component: LoginSessionComponent?
        get() = _component

    override fun createSession(token: String, tokenType: String) {
        _component = loginSessionComponentFactory.create(token, tokenType)
    }

    override fun closeSession() {
        _component = null
        // TODO should this happen here?
        session.clear()
    }
}