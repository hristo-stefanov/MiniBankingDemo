package hristostefanov.minibankingdemo.usecase

enum class ContinuationId {
    GetSummary_RetryLoading,
    GetSummary_LoginCredentialsEnsured,
    TransferRoundUp_SavingsGoalSelected,
    TransferRoundUp_Confirmed,
    CreateSavingsGoal_NameSubmitted
}