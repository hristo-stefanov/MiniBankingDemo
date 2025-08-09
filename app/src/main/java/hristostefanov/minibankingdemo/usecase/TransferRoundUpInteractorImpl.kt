package hristostefanov.minibankingdemo.usecase

import androidx.lifecycle.SavedStateHandle
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.entities.SavingsGoal
import hristostefanov.minibankingdemo.business.interactors.AddMoneyIntoGoalInteractor
import hristostefanov.minibankingdemo.usecase.input.TransferRoundUpInteractor
import hristostefanov.minibankingdemo.usecase.output.UserInterface
import hristostefanov.minibankingdemo.util.StringSupplier
import java.math.BigDecimal
import java.util.Currency

// TODO prefix with the interactor name for namespacing
private const val ACCOUNT_ID_KEY = "accountId"
private const val CURRENCY_KEY = "currency"
private const val ROUND_UP_AMOUNT_KEY = "roundUpAmount"
private const val SAVINGS_GOALS_KEY = "savingsGoals"
private const val SELECTED_SAVINGS_GOAL_KEY = "selectedSavingsGoal"

class TransferRoundUpInteractorImpl(
    private val savedStateHandle: SavedStateHandle,
    private val repository: Repository,
    private val lifecycle: InteractorLifecycleImpl,
    private val addMoneyIntoGoalInteractor: AddMoneyIntoGoalInteractor,
    private val stringSupplier: StringSupplier,
) : TransferRoundUpInteractor, InteractorLifecycle by lifecycle {

    // Note: internal for extensions
    override var accountId: String
        get() = savedStateHandle.get<String>(ACCOUNT_ID_KEY) ?: ""
        set(value) { savedStateHandle[ACCOUNT_ID_KEY] = value }

    override var accountCurrency: Currency
        get() = savedStateHandle.get<Currency>(CURRENCY_KEY) ?: Currency.getInstance("GBP")
        set(value) { savedStateHandle[CURRENCY_KEY] = value }

    private var roundUpAmount: BigDecimal
        get() = savedStateHandle.get<BigDecimal>(ROUND_UP_AMOUNT_KEY) ?: BigDecimal.ZERO
        set(value) { savedStateHandle[ROUND_UP_AMOUNT_KEY] = value }

    private var savingsGoals: List<SavingsGoal>
        get() = savedStateHandle.get<List<SavingsGoal>>(SAVINGS_GOALS_KEY) ?: emptyList()
        set(value) { savedStateHandle[SAVINGS_GOALS_KEY] = value }

    private var selectedSavingGoalId: String
        get() = savedStateHandle.get<String>(SELECTED_SAVINGS_GOAL_KEY) ?: ""
        set(value) { savedStateHandle[SELECTED_SAVINGS_GOAL_KEY] = value }

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
            savingsGoals = repository.findSavingGoals(accountId)
            // TODO what do we do with message strings? Which layer do they come from?
            userInterface.promptUserToSelectSavingsGoal("Select destination", savingsGoals)

            // TODO proper error handling
        } catch (e: ServiceException) {
            userInterface.presentMessage(e.localizedMessage)
        }
    }

    override suspend fun onSavingsGaolSelected(id: String, userInterface: UserInterface) {
        selectedSavingGoalId = id
        savingsGoals.find { it.id == id }?.let {
            userInterface.promptUserToConfirmTransfer(
                roundUpAmount,
                accountCurrency,
                it.name,
                ContinuationId.TransferRoundUp_Confirmed
            )
        }
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