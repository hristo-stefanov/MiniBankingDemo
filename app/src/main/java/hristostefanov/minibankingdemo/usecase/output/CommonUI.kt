package hristostefanov.minibankingdemo.usecase.output

interface CommonUI {
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
}