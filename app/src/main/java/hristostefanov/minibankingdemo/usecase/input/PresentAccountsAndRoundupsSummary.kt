package hristostefanov.minibankingdemo.usecase.input

import hristostefanov.minibankingdemo.usecase.output.UserInterface
import java.math.BigDecimal
import java.util.Currency

interface PresentAccountsAndRoundupsSummary {
    suspend operator fun invoke(userInterface: UserInterface): Result<Unit>
}