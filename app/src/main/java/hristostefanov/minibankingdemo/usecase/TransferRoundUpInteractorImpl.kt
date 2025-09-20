package hristostefanov.minibankingdemo.usecase

import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.interactors.AddMoneyIntoGoalInteractor
import hristostefanov.minibankingdemo.usecase.input.TransferRoundUpInteractor
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.StringSupplier
import java.math.BigDecimal
import java.util.Currency

class TransferRoundUpInteractorImpl(
    private val repository: Repository,
    private val lifecycle: InteractorLifecycleImpl,
    private val addMoneyIntoGoalInteractor: AddMoneyIntoGoalInteractor,
    private val stringSupplier: StringSupplier,
) : TransferRoundUpInteractor, InteractorLifecycle by lifecycle {

    override lateinit var accountId: String

    override lateinit var accountCurrency: Currency

    private lateinit var roundUpAmount: BigDecimal

    private lateinit var selectedSavingGoalId: String

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
            val savingsGoals = repository.findSavingGoals(accountId)

            // TODO what do we do with message strings? Which layer do they come from?
            userInterface.promptUserToSelectSavingsGoal("Select destination", savingsGoals)

            // TODO proper error handling
        } catch (e: ServiceException) {
            userInterface.presentMessage(e.localizedMessage)
        }
    }

    override suspend fun onSavingsGaolSelected(
        id: String,
        name: String,
        userInterface: UserInterface
    ) {
        selectedSavingGoalId = id
        userInterface.promptUserToConfirmTransfer(
            roundUpAmount,
            accountCurrency,
            name,
            ContinuationId.TransferRoundUp_Confirmed
        )
    }

    override suspend fun onTransferConfirmed(userInterface: UserInterface) {
        try {
            addMoneyIntoGoalInteractor.execute(
                accountId,
                selectedSavingGoalId,
                accountCurrency,
                roundUpAmount
            )

            userInterface.presentMessage(stringSupplier.get(R.string.success))

            userInterface.closeTransferRoundUpUI()

            // TODO proper error handling
        } catch (e: ServiceException) {
            e.localizedMessage?.let { userInterface.presentMessage(it) }
        }
    }
}