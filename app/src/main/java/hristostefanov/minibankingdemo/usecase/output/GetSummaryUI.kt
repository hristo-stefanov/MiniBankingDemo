package hristostefanov.minibankingdemo.usecase.output

interface GetSummaryUI : EnsureLoginCredentialsUI, StockUI {
    fun presentSummary(summary: Summary)
    suspend fun presentHintToReferesh()
}