package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.entities.SavingsGoal
import hristostefanov.minibankingdemo.business.interactors.ListSavingGoalsInteractor
import hristostefanov.minibankingdemo.usecase.input.TransferRoundUpInteractor
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import java.math.BigDecimal
import java.util.Currency
import javax.inject.Inject

class TransferRoundUpInteractorImpl @Inject constructor(
    private val lifecycle: InteractorLifecycleImpl,
    private val listSavingsGoalsInteractor: ListSavingGoalsInteractor
) : TransferRoundUpInteractor, InteractorLifecycle by lifecycle {

    private lateinit var accountId: String
    private lateinit var accountCurrency: Currency
    private lateinit var roundUpAmount: BigDecimal
    private lateinit var savingsGoals: List<SavingsGoal>

    override suspend fun start(
        accountId: String,
        accountCurrency: Currency,
        roundUpAmount: BigDecimal,
        userInterface: UserInterface
    ) {
        if (lifecycle.status == InteractorStatus.Started)
            throw IllegalStateException()

        this.accountId = accountId
        this.roundUpAmount = roundUpAmount
        this.accountCurrency = accountCurrency

        lifecycle.setStatus(InteractorStatus.Started)

        try {
            savingsGoals = listSavingsGoalsInteractor.execute(accountId)
            userInterface.promptUserToSelectSavingsGoal("Select destination", savingsGoals)

            // TODO proper error handling
        } catch (e: ServiceException) {
            userInterface.presentMessage(e.localizedMessage)
        }
    }

    override suspend fun onSavingsGaolSelected(id: String, userInterface: UserInterface) {
        savingsGoals.find { it.id == id }?.let {
            userInterface.promptUserToConfirmTransfer(roundUpAmount, accountCurrency, it.name)
        }
    }
}