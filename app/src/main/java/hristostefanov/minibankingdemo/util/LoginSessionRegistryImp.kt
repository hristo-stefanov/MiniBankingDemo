package hristostefanov.minibankingdemo.util

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginSessionRegistryImp @Inject constructor(
    private val loginSessionComponentFactory: LoginSessionComponent.Factory
) : LoginSessionRegistry {

    override var component: LoginSessionComponent? = null

    override fun createSession(token: String, tokenType: String) {
        component = loginSessionComponentFactory.create(token, tokenType)
    }

    override fun close() {
        component = null
    }
}