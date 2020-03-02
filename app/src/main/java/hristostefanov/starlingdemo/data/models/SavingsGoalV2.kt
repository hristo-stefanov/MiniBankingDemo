package hristostefanov.starlingdemo.data.models

data class SavingsGoalV2(
    val description: String?,
    val savingsGoalUid: String?,
    val name: String?,
    val target: CurrencyAndAmount?,
    val totalSaved: CurrencyAndAmount?,
    val savedPercentage: Int?
)