package hristostefanov.minibankingdemo.util

interface SessionRegistry {
    var sessionComponent: SessionComponent
    fun newSession()
}