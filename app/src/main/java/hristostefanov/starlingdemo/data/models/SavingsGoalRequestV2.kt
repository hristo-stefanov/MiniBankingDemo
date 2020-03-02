package hristostefanov.starlingdemo.data.models

data class SavingsGoalRequestV2(
    val name: String,
    val currency: String,
    val target: CurrencyAndAmount? = null,
    val base64EncodedPhoto: String? = null
)