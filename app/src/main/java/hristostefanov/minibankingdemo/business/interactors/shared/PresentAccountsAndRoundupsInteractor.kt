package hristostefanov.minibankingdemo.business.interactors.shared

import hristostefanov.minibankingdemo.business.dependences.Repository
import java.math.BigDecimal
import hristostefanov.minibankingdemo.business.calcAccountRoundUpInteractor
import java.time.OffsetDateTime

typealias CalcAccountRoundUpInteractor = suspend Repository.(accountId: String, since: OffsetDateTime) -> BigDecimal

class PresentAccountsAndRoundupsInteractor constructor(
    private val repository: Repository,
    val output: PresentAccountsAndRoundUpsOutputBoundary,
    val now: OffsetDateTime,
    val calcAccountRoundUpInteractorArg: CalcAccountRoundUpInteractor
    = { accountId, since -> calcAccountRoundUpInteractor(accountId, since) },
) {
    suspend fun execute() {

        val reportItems = repository.findAllAccounts().map { account ->
            val roundUp = repository.calcAccountRoundUpInteractorArg(account.id, now)

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