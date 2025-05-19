package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.business.dependences.Repository
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
    val calcAccountRoundUpInteractor: CalcAccountRoundUpInteractor,
) {
    suspend operator fun invoke() {

        val reportItems = repository.findAllAccounts().map { account ->
            val roundUp = calcAccountRoundUpInteractor(account.id, now)

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