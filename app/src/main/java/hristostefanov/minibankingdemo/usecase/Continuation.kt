package hristostefanov.minibankingdemo.usecase

data class Continuation(
    val id: ContinuationId,
    val params: List<Any> = emptyList(),
)
