package hristostefanov.minibankingdemo.usecase.input

sealed interface Outcome {
    class Completed<T>(val result: T): Outcome
    class Failed(val exception: Throwable): Outcome
    object Cancelled: Outcome
}