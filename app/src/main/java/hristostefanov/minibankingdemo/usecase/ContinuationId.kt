package hristostefanov.minibankingdemo.usecase

enum class ContinuationId {
    Startup_LoginCredentialsSubmit,
    GetSummary_RetryLoading,
    GetSummary_LoginCredentialsEnsured,
    TransferRoundUp_SavingsGoalSelected
}