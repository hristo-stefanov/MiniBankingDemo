package hristostefanov.minibankingdemo.usecase

import arrow.core.Either
import arrow.core.recover
import hristostefanov.minibankingdemo.business.calcAccountRoundUp
import hristostefanov.minibankingdemo.business.calcTransactionRoundUp
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.entities.Account
import hristostefanov.minibankingdemo.business.entities.SavingsGoal
import hristostefanov.minibankingdemo.business.entities.Transaction
import hristostefanov.minibankingdemo.business.isSpendingTransaction
import hristostefanov.minibankingdemo.usecase.input.Completion
import hristostefanov.minibankingdemo.usecase.input.EnsureLoginCredentialsInteractor
import hristostefanov.minibankingdemo.usecase.input.Status
import hristostefanov.minibankingdemo.usecase.input.Termination.Failure
import hristostefanov.minibankingdemo.usecase.input.ViewSummaryInteractor
import hristostefanov.minibankingdemo.usecase.input.status
import hristostefanov.minibankingdemo.usecase.output.Summary
import hristostefanov.minibankingdemo.usecase.output.ViewSummaryUI
import hristostefanov.minibankingdemo.util.Session
import kotlinx.coroutines.async
import java.math.BigDecimal
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Provider

typealias CalcSincePolicy = (OffsetDateTime) -> OffsetDateTime

class ViewSummaryInteractorImpl @Inject constructor(
    val session: Session,
    val nowProvider: Provider<OffsetDateTime>,
    private val viewSummaryUI: ViewSummaryUI,
    private val calcSincePolicy: @JvmSuppressWildcards CalcSincePolicy,
    private val ensureLoginCredentialsInteractor: EnsureLoginCredentialsInteractor,
) : ViewSummaryInteractor {


    override suspend fun invoke(): Status {
        // Note: withContex() is not appropriate here because it's intended to keep the caller's Job ability to cancel
        // the new coroutine, while we need to run the new coroutine a CoroutineScope with a cancellable Job.
        // See: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/with-context.html
        return session.requireTokenComponent.scope.async {
            // TODO this interactor changes the state, where do we do sope.async {} - here or there or both?
            ensureLoginCredentialsInteractor()
                .fold({ it.status() }, { execute() })
        }.await()
    }

    private suspend fun execute(): Status {
        return Either.catch {
            val now = nowProvider.get()
            val since = calcSincePolicy(now)
            val accountDetails = repository.findAllAccounts()
                .fold(emptyMap<Account, Pair<List<Transaction>, List<SavingsGoal>>>()) { acc, account ->
                    val transactions = repository.findTransactions(account.id, since)
                    val savingsGoals = repository.findSavingGoals(account.id)
                    val details = transactions to savingsGoals
                    acc + (account to details)
                }

            val summary = summarize(since, accountDetails)

            viewSummaryUI.presentSummary(summary)
            Completion
        }.recover { e ->
            raise(Failure(e))
        }
    }

    // TODO requireComponent is a pre-condition for this interactor
    private val repository: Repository
        get() = session.requireTokenComponent.repository
}


internal fun summarize(
    since: OffsetDateTime,
    dataset: Map<Account, Pair<List<Transaction>, List<SavingsGoal>>>,
    calcTransactionRoundUpPolicy: (Transaction) -> BigDecimal = ::calcTransactionRoundUp,
    isSpendingTransactionPolicy: (Transaction) -> Boolean = ::isSpendingTransaction,
): Summary {
    val items = dataset.entries.map { (account, accountDetails) ->
        val roundUp = calcAccountRoundUp(
            transactions = accountDetails.first,
            calcTransactionRoundUpPolicy = calcTransactionRoundUpPolicy,
            isSpendingTransactionPolicy = isSpendingTransactionPolicy
        )
        Summary.Item(
            accountId = account.id,
            number = account.accountNum,
            balance = account.balance,
            currency = account.currency,
            roundUp = roundUp,
            savingsGoals = accountDetails.second
        )
    }

    return Summary(since, items)
}