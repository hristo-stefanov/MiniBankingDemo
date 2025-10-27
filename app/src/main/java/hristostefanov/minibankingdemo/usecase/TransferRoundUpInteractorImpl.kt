package hristostefanov.minibankingdemo.usecase

import arrow.core.Either
import arrow.core.recover
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.usecase.input.Failure
import hristostefanov.minibankingdemo.usecase.input.TransferRoundUpInteractor
import hristostefanov.minibankingdemo.usecase.input.Status
import hristostefanov.minibankingdemo.usecase.output.StockUI
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.StringSupplier
import java.math.BigDecimal
import java.util.Currency
import javax.inject.Inject

class TransferRoundUpInteractorImpl @Inject constructor(
    private val loginSessionRegistry: LoginSessionRegistry,
    private val stringSupplier: StringSupplier,
    private val stockUI: StockUI
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

            stockUI.presentMessage(stringSupplier.get(R.string.success))
        }.recover { exception ->
            exception.localizedMessage?.let { stockUI.presentMessage(it) }
            Failure(exception)
        }
    }
}