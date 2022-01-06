package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.presentation.LoginViewModel
import hristostefanov.minibankingdemo.presentation.AccountsViewModel
import java.math.BigDecimal

/**
 * Automation interface for exercising interaction steps for the
 * user's journey thorough the application.
 * Such an interface is also called "Step library".
 *
 * The interaction happens through the presentation layer with stubbed
 * responses from the business and infrastructure layers.
 */
interface PresentationTestAutomation {
    // stubbing
    fun calculatedRoundUpIs(amount: BigDecimal)
    fun correctRefreshTokenIs(accessToken: String)
    fun savedRefreshTokenIs(refreshToken: String)
    fun accountIn(currencyCode: String)

    // exercising
    fun openAccountScreen(): AccountsViewModel
    fun openLoginScreen(): LoginViewModel
}

