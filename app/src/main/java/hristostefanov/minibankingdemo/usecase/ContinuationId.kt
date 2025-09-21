package hristostefanov.minibankingdemo.usecase

enum class ContinuationId {
    GetSummary_RetryLoading,
    TransferRoundUp_SavingsGoalSelected,
    TransferRoundUp_Confirmed,
    CreateSavingsGoal_NameSubmitted
}