package hristostefanov.minibankingdemo.usecase.input

import java.math.BigDecimal
import java.util.Currency

interface PresentAccountsAndRoundupsSummary {
    suspend operator fun invoke(): Result<Unit>
}