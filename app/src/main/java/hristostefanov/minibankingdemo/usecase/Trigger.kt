package hristostefanov.minibankingdemo.usecase

import java.math.BigDecimal
import java.util.Currency

sealed interface Trigger

data class TransferFromAccount(
    val accountId: String,
    val currency: Currency,
    val roundUpAmount: BigDecimal
): Trigger

data object CreateSavingsGoal : Trigger

data object CancelTransferRoundUp : Trigger

data object CancelCreateSavingsGoal : Trigger