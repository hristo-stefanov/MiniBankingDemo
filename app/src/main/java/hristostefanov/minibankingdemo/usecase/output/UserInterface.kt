package hristostefanov.minibankingdemo.usecase.output

import hristostefanov.minibankingdemo.business.entities.SavingsGoal
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Currency

// TODO there is no binding for this and view model inject the implementation directly
// how the instances of this interface should be created when navigation is not used
// for every user interaction?
interface UserInterface: EnsureLoginCredentialsUI, StockUI  {

    suspend fun promptUserToConfirmTransfer(
        amount: BigDecimal,
        currency: Currency,
        savingsGoalNam: String,
    )

    suspend fun promptUserToSelectSavingsGoal(message: String, savingsGoals: List<SavingsGoal>)
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
        val savingsGoals: List<SavingsGoal>
    )
}
