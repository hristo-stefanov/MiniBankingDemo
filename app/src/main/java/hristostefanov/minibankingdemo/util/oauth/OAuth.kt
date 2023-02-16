package hristostefanov.minibankingdemo.util.oauth

import retrofit2.HttpException
import retrofit2.http.*
import java.io.IOException
import com.google.gson.JsonSyntaxException

/**
 * NOTE: Any method can throw [HttpException] on non 2xx HTTP responses, [IOException]
 * on any network error including SSL/TLS related ones and [JsonSyntaxException] on Gson
 * serialization error.
 *
 */
interface OAuth {
    @POST("/oauth/access-token")
    @FormUrlEncoded
    suspend fun accessToken(
        @Field("client_id") client_id: String,
        // TODO The client secret must not be shared publicly, so you must make the call to our token exchange endpoint from your server. You must not send the client secret from a browser.
        @Field("client_secret") client_secret: String,
        @Field("grant_type") grant_type: String,
        @Field("refresh_token") refresh_token: String
    ): AccessTokenResponse
}