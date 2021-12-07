package hristostefanov.minibankingdemo.util.oauth

data class AccessTokenResponse(
    val access_token: String,
    val refresh_token: String,
    val token_type: String,
    val expires_in: Int,
    val scope: String
)
