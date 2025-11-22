package hristostefanov.minibankingdemo.presentation

import arrow.core.Either
import hristostefanov.minibankingdemo.presentation.DialogResult.*
import hristostefanov.minibankingdemo.usecase.input.Status

interface MainUI {
    /**
     * Ask to confirm retrying.
     *
     * @return true - confirmed, false - cancelled
     */
    suspend fun askToConfirmRetrying(errorMessage: String, isCancelable: Boolean): Either<Cancel, Confirm>

    /**
     * Present a message without waiting for acknowledgement.
     */
    suspend fun presentMessage(message: String)

    suspend fun presentStatus(status: Status)
    suspend fun presentErrorDialog(message: String)
    suspend fun promptUserToSubmitCredentials(): Either<Cancel, String>
}

interface DialogResult {
    object Cancel: DialogResult
    object Confirm: DialogResult
}
