package hristostefanov.starlingdemo.data.models

data class SavingsGoalV2(
    val description: String? = null,
    val savingsGoalUid: String? = null,
    val name: String? = null,
    val target: CurrencyAndAmount? = null,
    val totalSaved: CurrencyAndAmount? = null,
    val savedPercentage: Int? = null
)