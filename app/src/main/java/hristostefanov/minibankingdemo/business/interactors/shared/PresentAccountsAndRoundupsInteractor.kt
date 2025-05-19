package hristostefanov.minibankingdemo.business.interactors.shared

import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.usecase.CalcAccountRoundUpInteractor
import java.time.OffsetDateTime

//typealias CalcAccountRoundUpInteractor = suspend Repository.(accountId: String, since: OffsetDateTime) -> BigDecimal

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