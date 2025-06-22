package hristostefanov.minibankingdemo.usecase.output

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Currency

interface UserInterface {
    suspend fun promptUserToSubmitCredentials(): String
    fun present(summary: AccountsAndRoundUpsSummary)
    suspend fun promptUserToRetryRecovery(message: String): Boolean
}

data class AccountsAndRoundUpsSummary(
    val roundUpSice: OffsetDateTime,
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
