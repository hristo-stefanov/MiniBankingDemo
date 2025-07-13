package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.business.calcAccountRoundUp
import hristostefanov.minibankingdemo.business.calcTransactionRoundUp
import hristostefanov.minibankingdemo.business.dependences.APIException
import hristostefanov.minibankingdemo.business.dependences.AuthException
import hristostefanov.minibankingdemo.business.dependences.NetworkException
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.entities.Account
import hristostefanov.minibankingdemo.business.entities.Transaction
import hristostefanov.minibankingdemo.business.isSpendingTransaction
import hristostefanov.minibankingdemo.usecase.input.PresentAccountsAndRoundupsSummary
import hristostefanov.minibankingdemo.usecase.output.AccountsAndRoundUpsSummary
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Provider

typealias CalcSincePolicy = (OffsetDateTime) -> OffsetDateTime

class PresentAccountsAndRoundupsSummaryInteractor @Inject constructor(
    private val repository: Repository,
    val nowProvider: Provider<OffsetDateTime>,
    private val calcSincePolicy: @JvmSuppressWildcards CalcSincePolicy
) : PresentAccountsAndRoundupsSummary {

    private val _status = MutableStateFlow(InteractorStatus.Created)
    override val status: StateFlow<InteractorStatus> = _status.asStateFlow()

    override suspend fun start(userInterface: UserInterface) {
        if (status.value != InteractorStatus.Created)
            throw IllegalStateException()
        _status.emit(InteractorStatus.Started)
        execute(userInterface)
    }

    private suspend fun execute(userInterface: UserInterface) {
        val now = nowProvider.get()
        val since = calcSincePolicy(now)
        try {
            val dataset = repository.findAllAccounts()
                .fold(emptyMap<Account, List<Transaction>>()) { acc, account ->
                    val transactions = repository.findTransactions(account.id, since)
                    acc + (account to transactions)
                }

            val summary = summarize(since, dataset)
            userInterface.presentSummary(summary)
        } catch (e: ServiceException) {
            _status.emit(InteractorStatus.Failed)
            when (e) {
                is AuthException -> {
                    userInterface.presentMessage("Your credentials are invalid. You need to Log out first")
                }
                is APIException, is NetworkException -> {
                    userInterface.promptUserToRetryRecovery(
                        message = e.localizedMessage,
                        isCancellable = false,
                        continuationId = ContinuationId.PresentSummary_RetryLoading
                    )
                }
                else -> throw e
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
): AccountsAndRoundUpsSummary {
    val items = dataset.entries.map { (account, transactions) ->
        val roundUp = calcAccountRoundUp(
            transactions = transactions,
            calcTransactionRoundUpPolicy = calcTransactionRoundUpPolicy,
            isSpendingTransactionPolicy = isSpendingTransactionPolicy
        )
        AccountsAndRoundUpsSummary.Item(
            accountId = account.id,
            number = account.accountNum,
            balance = account.balance,
            currency = account.currency,
            roundUp = roundUp,
        )
    }

    return AccountsAndRoundUpsSummary(since, items)
}