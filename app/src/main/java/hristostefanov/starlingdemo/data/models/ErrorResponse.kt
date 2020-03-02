package hristostefanov.starlingdemo.data.models

data class ErrorResponse(
    val errors: List<ErrorDetail>?,
    val success: Boolean?
)