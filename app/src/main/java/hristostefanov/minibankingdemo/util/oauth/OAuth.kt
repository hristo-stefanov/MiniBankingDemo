package hristostefanov.minibankingdemo.util.oauth

import retrofit2.HttpException
import retrofit2.http.*

/**
 * NOTE: Any method can throw [HttpException] on non 2xx HTTP response
 */
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