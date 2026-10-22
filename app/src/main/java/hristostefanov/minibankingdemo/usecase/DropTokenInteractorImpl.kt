package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.usecase.input.Completion
import hristostefanov.minibankingdemo.usecase.input.DropTokenInteractor
import hristostefanov.minibankingdemo.usecase.input.Status
import hristostefanov.minibankingdemo.usecase.input.status
import hristostefanov.minibankingdemo.util.Session
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DropTokenInteractorImpl @Inject constructor(
    private val tokenStore: TokenStore,
    private val session: Session,
) : DropTokenInteractor {

    override suspend fun invoke(): Status {
        session.resetSession()
        tokenStore.setToken(null)

        return Completion.status()
    }
}