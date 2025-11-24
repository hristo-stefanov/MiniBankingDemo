package hristostefanov.minibankingdemo.presentation

import arrow.core.Either
import hristostefanov.minibankingdemo.presentation.DialogResult.*
import hristostefanov.minibankingdemo.usecase.input.Status

interface MainUI {
    suspend fun askForConfirmation(title: String?, message: String?, isCancelable: Boolean): Either<Cancel, OK>

    /**
     * Present a message without waiting for confirmation
     */
    fun presentMessage(message: String)

    fun presentStatus(status: Status)

    suspend fun presentErrorDialog(message: String)

    suspend fun promptUserToSubmitCredentials(): Either<Cancel, String>
}

interface DialogResult {
    object Cancel: DialogResult
    object OK: DialogResult
}
