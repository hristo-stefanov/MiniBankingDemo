package hristostefanov.minibankingdemo.presentation

import hristostefanov.minibankingdemo.usecase.output.PresentSummaryUI
import hristostefanov.minibankingdemo.usecase.output.StockUI
import hristostefanov.minibankingdemo.usecase.output.Summary
import hristostefanov.minibankingdemo.util.LoginSessionData
import hristostefanov.minibankingdemo.util.LoginSessionScope
import javax.inject.Inject

@LoginSessionScope
class PresentSummaryUiImpl @Inject constructor(
    private val stockUI: StockUI,
    private val loginSessionData: LoginSessionData
) : PresentSummaryUI {
    override fun presentSummary(summary: Summary) {
        loginSessionData.summary.value = summary
    }

    override suspend fun presentHintToReferesh() {
        stockUI.presentMessage("Use the Refresh command later")
    }

    override suspend fun presentInfoAboutAuthFailure() {
        stockUI.presentMessage("Your credentials are invalid. You need to Log out first")
    }
}