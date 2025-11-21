package hristostefanov.minibankingdemo.presentation

import hristostefanov.minibankingdemo.util.MainCommandChannel
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject
import hristostefanov.minibankingdemo.NavGraphXmlDirections
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.business.dependences.AuthException
import hristostefanov.minibankingdemo.usecase.input.Cancellation
import hristostefanov.minibankingdemo.usecase.input.Failure
import hristostefanov.minibankingdemo.usecase.input.Status
import hristostefanov.minibankingdemo.usecase.input.onCompletion
import hristostefanov.minibankingdemo.usecase.input.onTermination
import hristostefanov.minibankingdemo.util.StringSupplier
import javax.inject.Singleton
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton // scope the implementation so the two interface bindings resolve to the same instance
class MainUiImpl @Inject constructor(
    private val stringSupplier: StringSupplier,
    @MainCommandChannel
    private val mainCommandChannel: Channel<MainCommand>,
) : MainUI, MainUiContinuation {

    // TODO handle cancellation in a explicit way - with a tagged union or monad
    private var loginCredentialsContinuation: Continuation<String?>? = null

    // true - confirmed , false - cancelled
    private var retryRecoveryContinuation: Continuation<Boolean>? = null

    override suspend fun promptUserToSubmitCredentials(): String? {
        check(loginCredentialsContinuation == null) { "Nesting not supported" }

        mainCommandChannel.send(MainCommand.NavigateForward(NavGraphXmlDirections.toLoginDestination()))
        return suspendCoroutine {
            loginCredentialsContinuation = it
        }.also {
            loginCredentialsContinuation = null
        }
    }

    override suspend fun askToConfirmRetrying(message: String, isCancellable: Boolean): Boolean {
        check(retryRecoveryContinuation == null) { "Nesting not supported" }

        mainCommandChannel.send(
            MainCommand.NavigateForward(
                NavGraphXmlDirections.toRetryDialog(
                    // TODO how about cancelling uncancelable use case to close the app?
                    isCancelable = isCancellable,
                    message = message,
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
        checkNotNull(loginCredentialsContinuation).resume(null)
    }

    override fun onSubmitCredentials(credentials: String) {
        checkNotNull(loginCredentialsContinuation).resume(credentials)
    }

    override fun onCancelRetrying() {
        checkNotNull(retryRecoveryContinuation).resume(false)
    }

    override fun onConfirmRetrying() {
        checkNotNull(retryRecoveryContinuation).resume(true)
    }
}
