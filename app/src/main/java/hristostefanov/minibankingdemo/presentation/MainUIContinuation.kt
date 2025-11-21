package hristostefanov.minibankingdemo.presentation

interface MainUIContinuation {
    fun onCancelSubmitCredentials()
    fun onSubmitCredentials(credentials: String)

    fun onCancelRetrying()
    fun onConfirmRetrying()
}