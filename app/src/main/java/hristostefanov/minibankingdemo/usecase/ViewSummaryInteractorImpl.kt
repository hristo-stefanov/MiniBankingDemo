package hristostefanov.minibankingdemo.usecase

import arrow.core.Either
import arrow.core.recover
import hristostefanov.minibankingdemo.business.calcAccountRoundUp
import hristostefanov.minibankingdemo.business.calcTransactionRoundUp
import hristostefanov.minibankingdemo.business.dependences.APIException
import hristostefanov.minibankingdemo.business.dependences.AuthException
import hristostefanov.minibankingdemo.business.dependences.NetworkException
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.entities.Account
import hristostefanov.minibankingdemo.business.entities.SavingsGoal
import hristostefanov.minibankingdemo.business.entities.Transaction
import hristostefanov.minibankingdemo.business.isSpendingTransaction
import hristostefanov.minibankingdemo.usecase.input.Completion
import hristostefanov.minibankingdemo.usecase.input.EnsureLoginCredentialsInteractor
import hristostefanov.minibankingdemo.usecase.input.Failure
import hristostefanov.minibankingdemo.usecase.input.ViewSummaryInteractor
import hristostefanov.minibankingdemo.usecase.input.Status
import hristostefanov.minibankingdemo.usecase.input.status
import hristostefanov.minibankingdemo.usecase.output.ViewSummaryUI
import hristostefanov.minibankingdemo.usecase.output.Summary
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import java.math.BigDecimal
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Provider

typealias CalcSincePolicy = (OffsetDateTime) -> OffsetDateTime

class ViewSummaryInteractorImpl @Inject constructor(
    val loginSessionRegistry: LoginSessionRegistry,
    val nowProvider: Provider<OffsetDateTime>,
    private val calcSincePolicy: @JvmSuppressWildcards CalcSincePolicy,
    private val ensureLoginCredentialsInteractor: EnsureLoginCredentialsInteractor,
) : ViewSummaryInteractor {

    private val viewSummaryUI: ViewSummaryUI
        get() = loginSessionRegistry.requireComponent.viewSummaryUI

    override suspend fun invoke(): Status {
           return ensureLoginCredentialsInteractor().fold(
            {
                viewSummaryUI.presentHintToReferesh()
                Completion.status()
            },
            {
                execute()
            }
        )
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
        }.recover { e ->
            when (e) {
                is AuthException -> {
                    viewSummaryUI.presentInfoAboutAuthFailure()
                    Failure(e).status()
                }

                is APIException, is NetworkException -> {
                    Failure(e).status()
                }

                else -> {
                    // unexpected exception - crash
                    // TODO log it
                    throw e
                }
            }
        }
    }

    private val repository: Repository
        get() = loginSessionRegistry.requireComponent.repository
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