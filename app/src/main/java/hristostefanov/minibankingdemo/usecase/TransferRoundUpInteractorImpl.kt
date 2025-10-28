package hristostefanov.minibankingdemo.usecase

import arrow.core.Either
import arrow.core.recover
import hristostefanov.minibankingdemo.usecase.input.Failure
import hristostefanov.minibankingdemo.usecase.input.TransferRoundUpInteractor
import hristostefanov.minibankingdemo.usecase.input.Status
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import java.math.BigDecimal
import java.util.Currency
import javax.inject.Inject

class TransferRoundUpInteractorImpl @Inject constructor(
    private val loginSessionRegistry: LoginSessionRegistry,
) : TransferRoundUpInteractor {
    override suspend fun invoke(
        accountId: String,
        accountCurrency: Currency,
        roundUpAmount: BigDecimal,
        savingsGoalId: String
    ): Status {
        return  Either.catch {
            loginSessionRegistry.component!!.addMoneyIntoGoalInteractor.execute(
                accountId,
                savingsGoalId,
                accountCurrency,
                roundUpAmount
            )
        }.recover { exception ->
            raise(Failure(exception))
        }
    }
}