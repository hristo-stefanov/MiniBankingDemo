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
    lateinit var loginCredentialsContinuation: Continuation<String?>

    // TODO consider the case of multiple async operations asking for retry confirmation
    // true - confirmed , false - cancelled
    lateinit var retryRecoveryContinuation: Continuation<Boolean>

    override suspend fun promptUserToSubmitCredentials(): String? {
        mainCommandChannel.send(MainCommand.NavigateForward(NavGraphXmlDirections.toLoginDestination()))
        return suspendCoroutine {
            loginCredentialsContinuation = it
        }
    }

    override suspend fun askToConfirmRetrying(message: String, isCancellable: Boolean): Boolean {
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
                when(it) {
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
        loginCredentialsContinuation.resume(null)
    }

    override fun onSubmitCredentials(credentials: String) {
        loginCredentialsContinuation.resume(credentials)
    }

    override fun onCancelRetrying() {
        retryRecoveryContinuation.resume(false)
    }

    override fun onConfirmRetrying() {
        retryRecoveryContinuation.resume(true)
    }
}
