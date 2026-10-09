package hristostefanov.minibankingdemo.presentation

import hristostefanov.minibankingdemo.usecase.output.ViewSummaryUI
import hristostefanov.minibankingdemo.usecase.output.Summary
import hristostefanov.minibankingdemo.util.Session
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ViewSummaryUiImpl @Inject constructor(
    private val session: Session
) : ViewSummaryUI {
    override fun presentSummary(summary: Summary) {
        session.update { it.copy(summary = summary) }
    }
}