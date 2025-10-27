package hristostefanov.minibankingdemo.usecase.output

interface StockUI {
    /**
     * @return true - confirmed , false - cancelled
     */
    suspend fun promptUserToRetryRecovery(errorMessage: String, isCancellable: Boolean): Boolean

    /**
     * The UI will not prompt for acknowledgement.
     *
     * The calling interactor will not wait for continuation.
     */
    suspend fun presentMessage(message: String)
}