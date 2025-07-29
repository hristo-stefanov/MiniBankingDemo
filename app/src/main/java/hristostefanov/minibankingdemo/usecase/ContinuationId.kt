package hristostefanov.minibankingdemo.usecase

enum class ContinuationId {
    Startup_LoginCredentialsSubmit,
    GetSummary_RetryLoading,
    TransferRoundUp_AccountSelected,
    GetSummary_LoginCredentialsEnsured
}