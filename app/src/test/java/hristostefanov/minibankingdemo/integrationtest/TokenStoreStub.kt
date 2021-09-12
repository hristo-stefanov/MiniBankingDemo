package hristostefanov.minibankingdemo.integrationtest

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import javax.inject.Inject

class TokenStoreStub @Inject constructor(): TokenStore {
    override var token: String = ""
}