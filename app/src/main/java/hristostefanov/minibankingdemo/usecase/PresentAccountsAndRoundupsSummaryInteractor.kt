package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.business.calcAccountRoundUp
import hristostefanov.minibankingdemo.business.calcStartOfSevenDayWindowIncludingToday
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
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Currency
import javax.inject.Inject
import javax.inject.Provider

typealias CalcSincePolicy = (OffsetDateTime) -> OffsetDateTime

class PresentAccountsAndRoundupsSummaryInteractor @Inject constructor(
    private val repository: Repository,
    val nowProvider: Provider<OffsetDateTime>,
    private val calcSincePolicy: @JvmSuppressWildcards CalcSincePolicy
) : PresentAccountsAndRoundupsSummary {
    override suspend operator fun invoke(userInterface: UserInterface): Result<Unit> {
        val now = nowProvider.get()
        val since = calcSincePolicy(now)

        do {
            var isRetryConfirmed = false
            try {
                val dataset = repository.findAllAccounts()
                    .fold(emptyMap<Account, List<Transaction>>()) { acc, account ->
                        val transactions = repository.findTransactions(account.id, since)
                        acc + (account to transactions)
                    }

                val summary = summarize(since, dataset)
                userInterface.present(summary)
            } catch (e: ServiceException) {
                when (e) {
                    is AuthException -> return Result.failure(e)
                    is APIException, is NetworkException -> {
                        isRetryConfirmed =
                            userInterface.promptUserToRetryRecovery(e.localizedMessage)
                        if (!isRetryConfirmed)
                        // TODO shoudn't return a cancellation flag?
                            return Result.failure(e)
                    }

                    else -> throw e
                }
            }
        } while (isRetryConfirmed)

        return Result.success(Unit)
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