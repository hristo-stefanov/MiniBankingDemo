package hristostefanov.minibankingdemo.usecase

enum class InteractorStatus {
    // TODO Waiting?
    Created,
    Started,
    Completed,
    Failed,
    Cancelled;

    fun isFinished() = this in Completed .. Cancelled
    fun isActive() = this == Started
}