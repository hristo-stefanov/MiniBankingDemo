package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.usecase.input.TransferRoundUpInteractor
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.StringSupplier
import java.math.BigDecimal
import java.util.Currency
import javax.inject.Inject

class TransferRoundUpInteractorImpl @Inject constructor(
    private val loginSessionRegistry: LoginSessionRegistry,
    private val stringSupplier: StringSupplier,
) : TransferRoundUpInteractor {
    override suspend fun start(
        accountId: String,
        accountCurrency: Currency,
        roundUpAmount: BigDecimal,
        userInterface: UserInterface,
        savingsGoalId: String
    ): Outcome {
        try {
            loginSessionRegistry.component!!.addMoneyIntoGoalInteractor.execute(
                accountId,
                savingsGoalId,
                accountCurrency,
                roundUpAmount
            )

            userInterface.presentMessage(stringSupplier.get(R.string.success))

            return Outcome.Completed(Unit)
        } catch (e: ServiceException) {
            e.localizedMessage?.let { userInterface.presentMessage(it) }
            return Outcome.Failed(e)
        }
    }
}