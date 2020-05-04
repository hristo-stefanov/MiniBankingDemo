package hristostefanov.minibankingdemo.business.entities

import java.math.BigDecimal

data class Transaction(
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
    val title: String = ""
)

enum class Source {
    INTERNAL,
    EXTERNAL
}

enum class Status {
    SETTLED,
    UNSETTLED
}
