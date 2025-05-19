package hristostefanov.minibankingdemo.usecase

import android.icu.number.Precision.currency
import hristostefanov.minibankingdemo.business.calcAccountRoundUp
import hristostefanov.minibankingdemo.business.calcStartOfSevenDayWindowIncludingToday
import hristostefanov.minibankingdemo.business.calcTransactionRoundUp
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.entities.Account
import hristostefanov.minibankingdemo.business.entities.Transaction
import hristostefanov.minibankingdemo.business.isSpendingTransaction
import io.sentry.util.CollectionUtils.map
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Currency

//typealias CalcAccountRoundUpInteractor = suspend Repository.(accountId: String, since: OffsetDateTime) -> BigDecimal

interface PresentAccountsAndRoundUpsOutputBoundary {
    fun present(model: AccountsAndRoundUpsModel)
}

data class AccountsAndRoundUpsModel(
    val items: List<Item>
) {
    data class Item(
        val accountId: String,
        val number: String,
        val currency: Currency,
        val roundUp: BigDecimal,
        val balance: BigDecimal,
    )
}

class PresentAccountsAndRoundupsInteractor constructor(
    private val repository: Repository,
    val output: PresentAccountsAndRoundUpsOutputBoundary,
    val now: OffsetDateTime,
    private val calcTransactionRoundUpPolicy: (Transaction) -> BigDecimal = ::calcTransactionRoundUp,
    private val isSpendingTransactionPolicy: (Transaction) -> Boolean = ::isSpendingTransaction,
    private val calcSincePolicy: (OffsetDateTime) -> OffsetDateTime = ::calcStartOfSevenDayWindowIncludingToday
) {
    suspend operator fun invoke() {
        val since = calcSincePolicy(now)

        val dataset = repository.findAllAccounts()
            .fold(emptyMap<Account,List<Transaction>>()) { acc, account ->
                val transactions = repository.findTransactions(account.id, since)
                acc + (account to transactions)
            }

        val reportItems = dataset.entries.map { (account, transactions) ->
            val roundUp = calcAccountRoundUp(
                transactions = transactions,
                calcTransactionRoundUpPolicy = calcTransactionRoundUpPolicy,
                isSpendingTransactionPolicy = isSpendingTransactionPolicy)
            AccountsAndRoundUpsModel.Item(
                accountId =  account.id,
                number = account.accountNum,
                balance = account.balance,
                currency = account.currency,
                roundUp = roundUp,
            )
        }

        val model = AccountsAndRoundUpsModel(reportItems)
        output.present(model)
    }
}