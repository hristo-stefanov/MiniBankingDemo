package hristostefanov.minibankingdemo.presentation

import hristostefanov.minibankingdemo.usecase.output.ViewSummaryUI
import hristostefanov.minibankingdemo.usecase.output.Summary
import hristostefanov.minibankingdemo.util.LoginSessionData
import javax.inject.Inject

class ViewSummaryUiImpl @Inject constructor(
    private val loginSessionData: LoginSessionData
) : ViewSummaryUI {
    override fun presentSummary(summary: Summary) {
        loginSessionData.summary.value = summary
    }
}