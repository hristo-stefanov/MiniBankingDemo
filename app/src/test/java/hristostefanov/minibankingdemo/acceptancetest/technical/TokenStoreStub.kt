package hristostefanov.minibankingdemo.acceptancetest.technical

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import javax.inject.Inject

class TokenStoreStub @Inject constructor(): TokenStore {
    override var refreshToken: String = ""
}