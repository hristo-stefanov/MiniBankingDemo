package hristostefanov.minibankingdemo.presentation

import hristostefanov.minibankingdemo.usecase.output.ViewSummaryUI
import hristostefanov.minibankingdemo.usecase.output.Summary
import hristostefanov.minibankingdemo.util.Session
import javax.inject.Inject

class ViewSummaryUiImpl @Inject constructor(
    private val session: Session
) : ViewSummaryUI {
    override fun presentSummary(summary: Summary) {
        // TODO should we do scope.async { } or the caller should do it?
        session.updateState { it.copy(summary = summary) }
    }
}