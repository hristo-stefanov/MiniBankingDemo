package hristostefanov.minibankingdemo.cucumber

import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import javax.inject.Inject

class FakeTokenStore @Inject constructor(): TokenStore {
    override var token: String = ""
}