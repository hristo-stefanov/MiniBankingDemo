package hristostefanov.minibankingdemo.presentation

interface MainUiContinuation {
    fun onCancelSubmitCredentials()
    fun onSubmitCredentials(credentials: String)

    fun onCancelRetrying()
    fun onConfirmRetrying()
}