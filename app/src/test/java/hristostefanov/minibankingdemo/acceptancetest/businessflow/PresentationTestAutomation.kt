package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.presentation.AccessTokenViewModel
import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import java.math.BigDecimal

interface PresentationTestAutomation {
    fun login()
    fun calculatedRoundUpIs(amount: BigDecimal)
    fun openAccountScreen(): AccountsViewModel
    fun openLoginScreen(): AccessTokenViewModel
}

