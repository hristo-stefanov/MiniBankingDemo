package hristostefanov.minibankingdemo.util.oauth

data class AccessTokenRequest(
    val client_id: String,
    val client_secret: String,
    val grant_type: String,
    val refresh_token: String
)
