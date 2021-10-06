package hristostefanov.minibankingdemo.acceptancetest.technical

import hristostefanov.minibankingdemo.data.dependences.Service
import hristostefanov.minibankingdemo.data.models.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.mock.BehaviorDelegate

class ServiceStub(private val delegate: BehaviorDelegate<Service>): Service {

    var accounts: List<AccountV2> = emptyList()
    var balancePerAccountMap: Map<String, BalanceV2> = emptyMap()
    var accountIdentifierByAccountId: Map<String, AccountIdentifiers> = emptyMap()
    var feedItemsToAccountId: Map<String, List<FeedItem>> = emptyMap()
    var savingsGoalsToAccountId: Map<String, List<SavingsGoalV2>> = emptyMap()

    override suspend fun getAccounts(): Accounts {
        val accounts = Accounts(accounts)
        return delegate.returningResponse(accounts).getAccounts()
    }

    override suspend fun getBalance(accountUid: String): BalanceV2 {
        val balance = balancePerAccountMap[accountUid]
        return delegate.returningResponse(balance).getBalance(accountUid)
    }

    override suspend fun getIdentifiers(accountUid: String): AccountIdentifiers {
        val identifiers = accountIdentifierByAccountId[accountUid]
        return delegate.returningResponse(identifiers).getIdentifiers(accountUid)
    }

    override suspend fun getFeedItemsSince(
        accountUid: String,
        categoryUid: String,
        changesSince: String
    ): SimpleWrapperToAListOfFeedItems {
        val feedItems = feedItemsToAccountId[accountUid]
        val wrapper = SimpleWrapperToAListOfFeedItems(feedItems)
        return delegate.returningResponse(wrapper).getFeedItemsSince(accountUid, categoryUid, changesSince)
    }

    override suspend fun getSavingsGoals(accountUid: String): SavingsGoalsV2 {
        val savingsGoals = savingsGoalsToAccountId[accountUid]?.let {
            SavingsGoalsV2(it)
        }
        return delegate.returningResponse(savingsGoals).getSavingsGoals(accountUid)
    }

    override suspend fun createSavingsGoal(accountUid: String, request: SavingsGoalRequestV2) {
        if (request.name == "Pass") {
            delegate.returningResponse(Unit).createSavingsGoal(accountUid, request)
        } else {
            val errorResponse = """
            {
                "success": false,
                "errors": [
                    {
                        "message": "Demo error message. Use 'Pass' as name to succeed."
                    }
                ]
            }
        """.trimIndent()
            val responseBody = errorResponse.toResponseBody("application/json".toMediaType())
            val response = Response.error<ErrorResponse>(401, responseBody)
            throw HttpException(response)
        }
    }

    override suspend fun addMoneyIntoSavingsGoal(
        accountUid: String,
        savingsGoalUid: String,
        transferUid: String,
        request: TopUpRequestV2
    ) {
        // empty
    }
}