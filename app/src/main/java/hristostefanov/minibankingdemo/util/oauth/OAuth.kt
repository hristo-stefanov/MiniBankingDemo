package hristostefanov.minibankingdemo.util.oauth

import retrofit2.http.*

interface OAuth {
    @POST("/oauth/access-token")
    @FormUrlEncoded
    suspend fun accessToken(
        @Field("client_id") client_id: String,
        @Field("client_secret") client_secret: String,
        @Field("grant_type") grant_type: String,
        @Field("refresh_token") refresh_token: String
    ): AccessTokenResponse
}