package hristostefanov.minibankingdemo.util

import hristostefanov.minibankingdemo.data.dependences.Service
import hristostefanov.minibankingdemo.data.models.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.mock.BehaviorDelegate

class MockService(private val delegate: BehaviorDelegate<Service>): Service {
    override suspend fun getAccounts(): Accounts {
        val accounts = Accounts(
            listOf(
                AccountV2(
                    accountUid = "70ff2a60-e5fd-497b-96a8-278318d225f1",
                    currency = "GBP",
                    defaultCategory = "13abe154-ccfe-4300-a72e-55718f822fdd"),
                AccountV2(
                    accountUid = "51d2978e-0b99-4c54-a9f4-02ab890477cd",
                    currency = "EUR",
                    defaultCategory = "dd731fc8-9778-46de-ac3f-4137f757134e")
            )
        )
        return delegate.returningResponse(accounts).getAccounts()
    }

    override suspend fun getBalance(accountUid: String): BalanceV2 {
        val balance = when(accountUid) {
            "70ff2a60-e5fd-497b-96a8-278318d225f1" -> BalanceV2(effectiveBalance = CurrencyAndAmount("GBP", 12345))
            "51d2978e-0b99-4c54-a9f4-02ab890477cd" -> BalanceV2(effectiveBalance = CurrencyAndAmount("EUR", 67890))
            else -> throw AssertionError()
        }
        return delegate.returningResponse(balance).getBalance(accountUid)
    }

    override suspend fun getIdentifiers(accountUid: String): AccountIdentifiers {
        val identifiers = when (accountUid) {
            "70ff2a60-e5fd-497b-96a8-278318d225f1" -> AccountIdentifiers(accountIdentifier = "81108224")
            "51d2978e-0b99-4c54-a9f4-02ab890477cd" -> AccountIdentifiers(accountIdentifier = "08133217")
            else -> throw AssertionError()
        }
        return delegate.returningResponse(identifiers).getIdentifiers(accountUid)
    }

    override suspend fun getFeedItemsSince(
        accountUid: String,
        categoryUid: String,
        changesSince: String
    ): SimpleWrapperToAListOfFeedItems {
        val wrapper = when (accountUid) {
            "70ff2a60-e5fd-497b-96a8-278318d225f1" -> SimpleWrapperToAListOfFeedItems(listOf(
                FeedItem(
                    amount = CurrencyAndAmount("GBP", 123),
                    direction = "OUT",
                    source = "FASTER_PAYMENTS_OUT",
                    status = "SETTLED")
            ))

            "51d2978e-0b99-4c54-a9f4-02ab890477cd" -> SimpleWrapperToAListOfFeedItems(listOf(
                FeedItem(
                    amount = CurrencyAndAmount("EUR", 321),
                    direction = "OUT",
                    source = "FASTER_PAYMENTS_OUT",
                    status = "SETTLED")
            ))
            else -> throw AssertionError()
        }
        return delegate.returningResponse(wrapper).getFeedItemsSince(accountUid, categoryUid, changesSince)
    }

    override suspend fun getSavingsGoals(accountUid: String): SavingsGoalsV2 {
        val savingsGoals = when (accountUid) {
            "70ff2a60-e5fd-497b-96a8-278318d225f1" -> SavingsGoalsV2(listOf(SavingsGoalV2(savingsGoalUid = "216f22a6-9094-4666-8297-b75a1cf6b352", name = "New car")))
            "51d2978e-0b99-4c54-a9f4-02ab890477cd" -> SavingsGoalsV2(listOf(SavingsGoalV2(savingsGoalUid = "e63eb455-4a7e-45f8-a2ab-9e390e5ca9cb", name = "Exotic vacation")))
            else -> throw AssertionError()
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
            // TODO 401 (Unauthorized) doesn't seem appropriate
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