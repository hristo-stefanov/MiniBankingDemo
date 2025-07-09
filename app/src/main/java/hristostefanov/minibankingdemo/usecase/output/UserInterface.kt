package hristostefanov.minibankingdemo.usecase.output

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Currency

interface UserInterface {
    suspend fun promptUserToSubmitCredentials()
    fun present(summary: AccountsAndRoundUpsSummary)
    suspend fun promptUserToRetryRecovery(message: String, continuationId: String)
}

data class AccountsAndRoundUpsSummary(
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
