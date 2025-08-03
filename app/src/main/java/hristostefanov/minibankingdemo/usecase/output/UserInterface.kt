package hristostefanov.minibankingdemo.usecase.output

import hristostefanov.minibankingdemo.business.entities.SavingsGoal
import hristostefanov.minibankingdemo.usecase.ContinuationId
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Currency

interface UserInterface {

    /**
     * Non cancellable.
     *
     * Continuation must pass `loginCredentials: String`
     */
    suspend fun promptUserToSubmitCredentials(continuationId: ContinuationId)

    /**
     * Optionally cancellable.
     *
     * Continuation must pass `confirmedOrCancelled: Boolean`
     *
     * The interactor should stay active until receving continuation as the
     * the interactor state and UI are in sync.
     */
    suspend fun promptUserToRetryRecovery(message: String, isCancellable: Boolean, continuationId: ContinuationId)

    suspend fun promptUserToConfirmTransfer(
        amount: BigDecimal,
        currency: Currency,
        savingsGoalNam: String,
        continuationId: ContinuationId
    )

    /**
     * Will not make savable state changes, such a navigation or displaying a dialog,
     * to keep the UI state and interactor state in sync in case of process death.
     */
    fun presentSummary(summary: Summary)

    /**
     * The UI will not prompt for acknowledgement nor will make savable state changes
     * (such as navigation or displaying a dialog) to keep the state in sync
     * with the interactor in case of process death.
     *
     * The calling interactor will not wait for continuation.
     */
    suspend fun presentMessage(message: String)

    suspend fun promptUserToSelectSavingsGoal(message: String, savingsGoals: List<SavingsGoal>)

    suspend fun closeTransferRoundUpUI()
}

data class Summary(
    val roundUpSince: OffsetDateTime,
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
