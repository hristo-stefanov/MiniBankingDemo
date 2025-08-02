package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.business.calcAccountRoundUp
import hristostefanov.minibankingdemo.business.calcTransactionRoundUp
import hristostefanov.minibankingdemo.business.dependences.APIException
import hristostefanov.minibankingdemo.business.dependences.AuthException
import hristostefanov.minibankingdemo.business.dependences.NetworkException
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.entities.Account
import hristostefanov.minibankingdemo.business.entities.Transaction
import hristostefanov.minibankingdemo.business.isSpendingTransaction
import hristostefanov.minibankingdemo.usecase.input.EnsureLoginCredentialsInteractor
import hristostefanov.minibankingdemo.usecase.input.GetSummaryInteractor
import hristostefanov.minibankingdemo.usecase.output.Summary
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import java.math.BigDecimal
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Provider

typealias CalcSincePolicy = (OffsetDateTime) -> OffsetDateTime

class GetSummaryInteractorImpl @Inject constructor(
    val loginSessionRegistry: LoginSessionRegistry,
    val nowProvider: Provider<OffsetDateTime>,
    private val calcSincePolicy: @JvmSuppressWildcards CalcSincePolicy,
    private val ensureLoginCredentialsInteractor: EnsureLoginCredentialsInteractor,
    private val lifecycle: InteractorLifecycleImpl,
) : GetSummaryInteractor, InteractorLifecycle by lifecycle  {

    override suspend fun start(userInterface: UserInterface) {
        if (lifecycle.status == InteractorStatus.Started)
            throw IllegalStateException()
        lifecycle.setStatus(InteractorStatus.Started)

        ensureLoginCredentialsInteractor.start(userInterface, ContinuationId.GetSummary_LoginCredentialsEnsured)
    }

    override suspend fun onLoginCredentialsEnsured(userInterface: UserInterface) {
        execute(userInterface)
    }

    private suspend fun execute(userInterface: UserInterface) {
        val now = nowProvider.get()
        val since = calcSincePolicy(now)
        try {
            loginSessionRegistry.component?.repository?.let { repository ->
                val dataset = repository.findAllAccounts()
                    .fold(emptyMap<Account, List<Transaction>>()) { acc, account ->
                        val transactions = repository.findTransactions(account.id, since)
                        acc + (account to transactions)
                    }

                val summary = summarize(since, dataset)
                userInterface.presentSummary(summary)
            }
            lifecycle.setStatus(InteractorStatus.Completed)
        } catch (e: ServiceException) {
            when (e) {
                is AuthException -> {
                    userInterface.presentMessage("Your credentials are invalid. You need to Log out first")
                }
                is APIException, is NetworkException -> {
                    userInterface.promptUserToRetryRecovery(
                        message = e.localizedMessage,
                        isCancellable = false,
                        continuationId = ContinuationId.GetSummary_RetryLoading
                    )
                }
                else -> {
                    lifecycle.setStatus(InteractorStatus.Failed)
                    throw e
                }
            }
        }
    }

    override suspend fun onRetryLoading(userInterface: UserInterface) {
        // TODO retry confirmation result? When to cancel the interactor?
        // what would happen to the status of StartupInteractor when
        // called from there? And what about the status tracking in
        // MainViewModel?
        execute(userInterface)
    }
}

internal fun summarize(
    since: OffsetDateTime,
    dataset: Map<Account, List<Transaction>>,
    calcTransactionRoundUpPolicy: (Transaction) -> BigDecimal = ::calcTransactionRoundUp,
    isSpendingTransactionPolicy: (Transaction) -> Boolean = ::isSpendingTransaction,
): Summary {
    val items = dataset.entries.map { (account, transactions) ->
        val roundUp = calcAccountRoundUp(
            transactions = transactions,
            calcTransactionRoundUpPolicy = calcTransactionRoundUpPolicy,
            isSpendingTransactionPolicy = isSpendingTransactionPolicy
        )
        Summary.Item(
            accountId = account.id,
            number = account.accountNum,
            balance = account.balance,
            currency = account.currency,
            roundUp = roundUp,
        )
    }

    return Summary(since, items)
}