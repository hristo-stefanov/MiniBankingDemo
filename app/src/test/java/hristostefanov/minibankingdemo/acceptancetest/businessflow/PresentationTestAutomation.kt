package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import java.math.BigDecimal

interface PresentationTestAutomation {
    fun login()
    fun theCalculatedRoundUpIs(amount: BigDecimal)
    fun openAccountScreen(): AccountsViewModel
}

