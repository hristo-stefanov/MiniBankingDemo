package hristostefanov.minibankingdemo.presentation

import arrow.core.Either
import hristostefanov.minibankingdemo.util.NavigationChannel
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
import hristostefanov.minibankingdemo.usecase.output.EnsureLoginCredentialsUI
import hristostefanov.minibankingdemo.usecase.output.StockUI
import hristostefanov.minibankingdemo.util.StringSupplier
import javax.inject.Singleton
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

@Singleton
class UserInterfaceImpl @Inject constructor(
    private val stringSupplier: StringSupplier,
    @NavigationChannel
    private val navigationChannel: Channel<Navigation>,
    // TODO shouldn't we break this implementation into separate ones for each interface?
) : EnsureLoginCredentialsUI, StockUI {

    // TODO handle cancellation in a explicit way - with a tagged union or monad
    lateinit var loginCredentialsContinuation: Continuation<String?>

    // TODO consider the case of multiple async operations asking for retry confirmation
    // true - confirmed , false - cancelled
    lateinit var retryRecoveryContinuation: Continuation<Boolean>

    override suspend fun promptUserToSubmitCredentials(): String? {
        navigationChannel.send(Navigation.Forward(NavGraphXmlDirections.toLoginDestination()))
        return suspendCoroutine {
            loginCredentialsContinuation = it
        }
    }


    override suspend fun promptUserToRetryRecovery(message: String, isCancellable: Boolean): Boolean {
        navigationChannel.send(
            Navigation.Forward(
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
        // TODO make it not navigate - display a temp
        navigationChannel.send(
            Navigation.Message(message)
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
}