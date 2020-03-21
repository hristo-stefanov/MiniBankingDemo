package hristostefanov.starlingdemo.data.models

data class BalanceV2(
    val clearedBalance: CurrencyAndAmount? = null,
    val effectiveBalance: CurrencyAndAmount? = null,
    val pendingTransactions: CurrencyAndAmount? = null,
    val availableToSpend: CurrencyAndAmount? = null,
    val acceptedOverdraft: CurrencyAndAmount? = null,
    val amount: CurrencyAndAmount? = null
)