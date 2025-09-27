package hristostefanov.minibankingdemo.usecase.output

interface EnsureLoginCredentialsUI {
    /**
     * @return null if cancelled and the credentials otherwise
     */
    suspend fun promptUserToSubmitCredentials(): String?
}