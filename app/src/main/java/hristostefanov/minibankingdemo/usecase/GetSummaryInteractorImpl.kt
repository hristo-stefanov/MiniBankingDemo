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
) : GetSummaryInteractor {

    override suspend fun start(userInterface: UserInterface): Outcome {
        val outcome = ensureLoginCredentialsInteractor.start(userInterface)
        if (outcome is Outcome.Completed<*>) {
            return execute(userInterface)
        } else {
            userInterface.presentHintToReferesh()
            return outcome
        }
    }

    private suspend fun execute(userInterface: UserInterface): Outcome {
        var shouldRetry: Boolean
        do {
            shouldRetry = false
            try {
                loginSessionRegistry.component?.repository?.let { repository ->
                    val now = nowProvider.get()
                    val since = calcSincePolicy(now)
                    val dataset = repository.findAllAccounts()
                        .fold(emptyMap<Account, List<Transaction>>()) { acc, account ->
                            val transactions = repository.findTransactions(account.id, since)
                            acc + (account to transactions)
                        }

                    val summary = summarize(since, dataset)
                    userInterface.presentSummary(summary)
                }
                return Outcome.Completed(Unit)
            } catch (e: ServiceException) {
                when (e) {
                    is AuthException -> {
                        userInterface.presentMessage("Your credentials are invalid. You need to Log out first")
                        return Outcome.Failed(e)
                    }

                    is APIException, is NetworkException -> {
                        val isConfirmed = userInterface.promptUserToRetryRecovery(
                            message = e.localizedMessage,
                            isCancellable = true,
                        )
                        if (isConfirmed) {
                            shouldRetry = true
                        } else {
                            userInterface.presentHintToReferesh()
                            return Outcome.Cancelled
                        }
                    }

                    else -> {
                        // unexpected exception - crash
                        // TODO log it
                        throw e
                    }
                }
            }

        } while (shouldRetry)

        // should not come here
        return Outcome.Cancelled
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