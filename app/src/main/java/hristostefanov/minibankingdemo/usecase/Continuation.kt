package hristostefanov.minibankingdemo.usecase

data class Continuation(
    val id: ContinuationId,
    val param: String? = null
)
