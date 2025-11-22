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
import kotlinx.coroutines.channels.Channel
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

    private var loginCredentialsContinuation: Continuation<Either<Cancel, String>>? = null

    private var retryRecoveryContinuation: Continuation<Either<Cancel, Confirm>>? = null

    override suspend fun promptUserToSubmitCredentials(): Either<Cancel, String> {
        check(loginCredentialsContinuation == null) { "Nesting not supported" }

        mainCommandChannel.send(MainCommand.NavigateForward(NavGraphXmlDirections.toLoginDestination()))
        return suspendCoroutine {
            loginCredentialsContinuation = it
        }.also {
            loginCredentialsContinuation = null
        }
    }

    override suspend fun askToConfirmRetrying(errorMessage: String, isCancelable: Boolean): Either<Cancel, Confirm> {
        check(retryRecoveryContinuation == null) { "Nesting not supported" }

        mainCommandChannel.send(
            MainCommand.NavigateForward(
                NavGraphXmlDirections.toRetryDialog(
                    isCancelable = isCancelable,
                    message = errorMessage,
                )
            )
        )
        return suspendCoroutine {
            retryRecoveryContinuation = it
        }.also {
            retryRecoveryContinuation = null
        }
    }

    override suspend fun presentMessage(message: String) {
        mainCommandChannel.send(
            MainCommand.ShowSnackbar(message)
        )
    }

    override suspend fun presentErrorDialog(message: String) {
        mainCommandChannel.send(
            MainCommand.ShowErrorDialog(message)
        )
    }

    override suspend fun presentStatus(status: Status) {
        status
            .onCompletion { presentMessage(stringSupplier.get(R.string.success)) }
            .onTermination {
                when (it) {
                    Cancellation -> presentMessage("Cancelled")
                    is Failure -> {
                        if (it.exception is AuthException) {
                            presentMessage("Your credentials are invalid. You need to Log out first")
                        } else {
                            presentMessage("Failure: ${it.exception.localizedMessage}")
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

    override fun onCancelRetrying() {
        checkNotNull(retryRecoveryContinuation).resume(Cancel.left())
    }

    override fun onConfirmRetrying() {
        checkNotNull(retryRecoveryContinuation).resume(Confirm.right())
    }
}
