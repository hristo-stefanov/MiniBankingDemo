package hristostefanov.minibankingdemo.business.interactors.shared

import hristostefanov.minibankingdemo.business.calcStartOfSevenDayWindowIncludingToday
import hristostefanov.minibankingdemo.business.dependences.Repository
import java.math.BigDecimal
import java.time.OffsetDateTime

class PresentAccountsAndRoundupsInteractor constructor(
    private val repository: Repository,
    val output: PresentAccountsAndRoundUpsOutputBoundary,
    val now: OffsetDateTime,
    val calcAccountRoundUpPolicy: suspend (repository: Repository, accountId: String, since: OffsetDateTime) -> BigDecimal,
) {
    suspend fun execute() {

        val since = calcStartOfSevenDayWindowIncludingToday(now)

        val reportItems = repository.findAllAccounts().map { account ->
            val roundUp = calcAccountRoundUpPolicy(repository, account.id, since)

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