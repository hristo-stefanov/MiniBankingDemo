package hristostefanov.minibankingdemo.usecase.output

import hristostefanov.minibankingdemo.business.entities.SavingsGoal
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Currency

interface GetSummaryUI {
    fun presentSummary(summary: Summary)
    suspend fun presentHintToReferesh()
    suspend fun presentInfoAboutAuthFailure()
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
