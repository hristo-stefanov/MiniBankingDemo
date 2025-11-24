package hristostefanov.minibankingdemo.presentation

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import hristostefanov.minibankingdemo.NavGraphXmlDirections
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.business.dependences.AuthException
import hristostefanov.minibankingdemo.presentation.DialogResult.*
import hristostefanov.minibankingdemo.usecase.input.Termination.Cancellation
import hristostefanov.minibankingdemo.usecase.input.Termination.Failure
import hristostefanov.minibankingdemo.usecase.input.Status
import hristostefanov.minibankingdemo.usecase.input.onCompletion
import hristostefanov.minibankingdemo.usecase.input.onTermination
import hristostefanov.minibankingdemo.util.MainCommandChannel
import hristostefanov.minibankingdemo.util.StringSupplier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@Singleton // scope the implementation so the two interface bindings resolve to the same instance
class MainUIImpl @Inject constructor(
    private val stringSupplier: StringSupplier,
    @MainCommandChannel
    private val mainCommandChannel: Channel<MainCommand>,
) : MainUI, MainUIContinuation {

    // No need to cancel this scope, it'll be torn down with the process
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var loginCredentialsContinuation: Continuation<Either<Cancel, String>>? = null

    private var confirmationContinuation: Continuation<Either<Cancel, OK>>? = null

    override suspend fun promptUserToSubmitCredentials(): Either<Cancel, String> {
        check(loginCredentialsContinuation == null) { "Nesting not supported" }

        mainCommandChannel.send(MainCommand.NavigateForward(NavGraphXmlDirections.toLoginDestination()))
        return suspendCoroutine {
            loginCredentialsContinuation = it
        }.also {
            loginCredentialsContinuation = null
        }
    }

    override suspend fun askForConfirmation(title: String?, message: String?, isCancelable: Boolean): Either<Cancel, OK> {
        check(confirmationContinuation == null) { "Nesting not supported" }

        mainCommandChannel.send(
            MainCommand.ShowConfirmationDialog(title, message, isCancelable)
        )
        return suspendCoroutine {
            confirmationContinuation = it
        }.also {
            confirmationContinuation = null
        }
    }

    override fun presentMessage(message: String) {
        coroutineScope.launch {
            mainCommandChannel.send(
                MainCommand.ShowSnackbar(message)
            )
        }
    }

    override suspend fun presentErrorDialog(message: String) {
        askForConfirmation("Error", message, false)
    }

    override fun presentStatus(status: Status) {
        status
            .onCompletion { presentMessage(stringSupplier.get(R.string.success)) }
            .onTermination {
                when (it) {
                    Cancellation -> presentMessage(stringSupplier.get(R.string.cancelled))
                    is Failure -> {
                        if (it.exception is AuthException) {
                            presentMessage(stringSupplier.get(R.string.invalid_credentials))
                        } else {
                            presentMessage(stringSupplier.get(R.string.failure, it.exception.localizedMessage))
                        }
                    }
                }
            }
    }

    override fun onCancelSubmitCredentials() {
        checkNotNull(loginCredentialsContinuation).resume(Cancel.left())
    }

    override fun onSubmitCredentials(credentials: String) {
        checkNotNull(loginCredentialsContinuation).resume(credentials.right())
    }

    override fun onCancel() {
        checkNotNull(confirmationContinuation).resume(Cancel.left())
    }

    override fun onConfirm() {
        checkNotNull(confirmationContinuation).resume(OK.right())
    }
}
