package hristostefanov.minibankingdemo.util

interface ISessionRegistry {
    var sessionComponent: SessionComponent
    fun newSession()
}