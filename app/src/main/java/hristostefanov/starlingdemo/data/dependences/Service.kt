package hristostefanov.starlingdemo.data.dependences

import hristostefanov.starlingdemo.data.models.*
import retrofit2.http.*

interface Service {
    @GET("/api/v2/accounts")
    suspend fun getAccounts(): Accounts

    @GET("/api/v2/accounts/{accountUid}/balance")
    suspend fun getBalance(@Path("accountUid") accountUid: String): BalanceV2

    @GET("/api/v2/accounts/{accountUid}/identifiers")
    suspend fun getIdentifiers(@Path("accountUid") accountUid: String): AccountIdentifiers

    /**
     * On HTTP status code 400 responses with [ErrorResponse]
     */
    @GET("/api/v2/feed/account/{accountUid}/category/{categoryUid}")
    suspend fun getFeedItemsSince(
        @Path("accountUid") accountUid: String,
        @Path("categoryUid") categoryUid: String, @Query("changesSince") changesSince: String
    ): SimpleWrapperToAListOfFeedItems

    @GET("/api/v2/account/{accountUid}/savings-goals")
    suspend fun getSavingsGoals(@Path("accountUid") accountUid: String): SavingsGoalsV2

    /**
     * On HTTP status code 400 responses with [ErrorResponse]
     */
    @PUT("/api/v2/account/{accountUid}/savings-goals")
    suspend fun createSavingsGoal(@Path("accountUid") accountUid: String, @Body request: SavingsGoalRequestV2)

    /**
     * On HTTP status code 400 responses with [ErrorResponse]
     */
    @PUT("/api/v2/account/{accountUid}/savings-goals/{savingsGoalUid}/add-money/{transferUid}")
    suspend fun addMoneyIntoSavingsGoal(
        @Path("accountUid") accountUid: String,
        @Path("savingsGoalUid") savingsGoalUid: String,
        @Path("transferUid") transferUid: String,
        @Body request: TopUpRequestV2
    )
}