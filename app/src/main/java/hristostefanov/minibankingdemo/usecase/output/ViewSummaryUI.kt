package hristostefanov.minibankingdemo.usecase.output

import hristostefanov.minibankingdemo.business.entities.SavingsGoal
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Currency

interface ViewSummaryUI {
    fun presentSummary(summary: Summary)
}

data class Summary(
    val roundUpSince: OffsetDateTime,
    val items: List<Item>
) {
   // TODO Can't we use Account from the entities package like SavingsGoal?
    data class Item(
        val accountId: String,
        val number: String,
        val currency: Currency,
        val roundUp: BigDecimal,
        val balance: BigDecimal,
        val savingsGoals: List<SavingsGoal>
    )
}
