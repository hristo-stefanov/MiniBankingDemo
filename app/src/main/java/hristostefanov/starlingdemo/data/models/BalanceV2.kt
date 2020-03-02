package hristostefanov.starlingdemo.data.models

data class BalanceV2(
    val clearedBalance: CurrencyAndAmount?,
    val effectiveBalance: CurrencyAndAmount?,
    val pendingTransactions: CurrencyAndAmount?,
    val availableToSpend: CurrencyAndAmount?,
    val acceptedOverdraft: CurrencyAndAmount?,
    val amount: CurrencyAndAmount?
)