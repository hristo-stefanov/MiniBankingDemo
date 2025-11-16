package hristostefanov.minibankingdemo.presentation

import hristostefanov.minibankingdemo.usecase.input.Status

interface MainUI {
    /**
     * Ask to confirm retrying.
     *
     * @return true - confirmed, false - cancelled
     */
    suspend fun askToConfirmRetrying(errorMessage: String, isCancellable: Boolean): Boolean

    /**
     * Present a message without waiting for acknowledgement.
     */
    suspend fun presentMessage(message: String)

    suspend fun presentStatus(status: Status)
    suspend fun presentErrorDialog(message: String)
    suspend fun promptUserToSubmitCredentials(): String?
}