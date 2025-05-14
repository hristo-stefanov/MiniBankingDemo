package hristostefanov.minibankingdemo.business.entities

import java.math.BigDecimal

data class Transaction(
    // TODO positive and negative amount is not appropriate for a transaction. Direction
    // can be use for clarity. The visual formatting can vary from "-100", "100 -" to "+200"
    /**
     * The amount of the transaction in the **main currency unit**, e.g. pound for GBP.
     *
     * The value is positive for inbound transactions and negative for
     * outbound transactions.
     *
     * The currency is the currency of the [Account]
     */
    val amount: BigDecimal,
    val status: Status,
    val source: Source,
    val title: String = "",
    val id: String = "",
)

enum class Source {
    INTERNAL,
    EXTERNAL
}

enum class Status {
    SETTLED,
    UNSETTLED
}
