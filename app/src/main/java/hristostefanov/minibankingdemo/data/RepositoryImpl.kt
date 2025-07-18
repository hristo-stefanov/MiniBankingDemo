package hristostefanov.minibankingdemo.data

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import hristostefanov.minibankingdemo.business.dependences.APIException
import hristostefanov.minibankingdemo.business.dependences.AuthException
import hristostefanov.minibankingdemo.business.dependences.NetworkException
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.entities.*
import hristostefanov.minibankingdemo.data.dependences.Service
import hristostefanov.minibankingdemo.data.models.*
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val service: Service,
    private val gson: Gson
) : Repository {

    // NOTE: Service is marked as @AnyThread so we don't need to care about main-safety in this class

    @Throws(ServiceException::class)
    override suspend fun findAllAccounts(): List<Account> {
        try {
            return service.getAccounts().accounts?.mapNotNull { account ->
                account.accountUid?.let { accountUid ->
                    val accountNum = service.getIdentifiers(accountUid).accountIdentifier
                    val effectiveBalance = service.getBalance(accountUid).effectiveBalance
                    if (
                        accountNum != null
                        && effectiveBalance != null
                        && account.currency != null
                        && account.defaultCategory != null
                    ) {
                        val decimalPlaces =
                            Currency.getInstance(account.currency).defaultFractionDigits
                        val balance =
                            BigDecimal(effectiveBalance.minorUnits.toBigInteger(), decimalPlaces)
                        Account(
                            accountUid,
                            accountNum,
                            account.defaultCategory,
                            Currency.getInstance(account.currency),
                            balance
                        )
                    } else {
                        null
                    }
                }
            } ?: emptyList()
        } catch (e: Exception) {
            throw e.toServiceException(gson)
        }
    }

    @Throws(ServiceException::class)
    override suspend fun findTransactions(
        accountId: String,
        since: OffsetDateTime,
    ): List<Transaction> {
        try {
            val account =
                service.getAccounts().accounts?.firstOrNull { it.accountUid == accountId }
            return if (account?.currency != null && account.defaultCategory != null) {
                val decimalPlaces = Currency.getInstance(account.currency).defaultFractionDigits

                // TODO add spec/test to cover this logic
                val changesSinceAsUTCDateTime = since.atZoneSameInstant(
                    ZoneOffset.UTC
                ).toString()
                service.getFeedItemsSince(accountId, account.defaultCategory, changesSinceAsUTCDateTime)
                    .feedItems
                    ?.map { it.toTransaction(decimalPlaces) }
                    ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            throw e.toServiceException(gson)
        }
    }

    @Throws(ServiceException::class)
    override suspend fun findSavingGoals(accountId: String): List<SavingsGoal> {
        try {
            return service.getSavingsGoals(accountId)
                .savingsGoalList.mapNotNull { it.toSavingGoal() }
        } catch (e: Exception) {
            throw e.toServiceException(gson)
        }
    }

    @Throws(ServiceException::class)
    override suspend fun createSavingsGoal(name: String, accountId: String, currency: Currency) {
        try {
            service.createSavingsGoal(accountId, SavingsGoalRequestV2(name, currency.currencyCode))
        } catch (e: Exception) {
            throw e.toServiceException(gson)
        }
    }

    @Throws(ServiceException::class)
    override suspend fun addMoneyIntoSavingsGoal(
        accountId: String,
        savingsGoalId: String,
        currency: Currency,
        amount: BigDecimal,
        transferId: UUID
    ) {
        try {
            val minorUnits =
                amount.scaleByPowerOfTen(currency.defaultFractionDigits).longValueExact()
            val request = TopUpRequestV2(CurrencyAndAmount(currency.currencyCode, minorUnits))
            service.addMoneyIntoSavingsGoal(
                accountId,
                savingsGoalId,
                transferId.toString(),
                request
            )
        } catch (e: Exception) {
            throw e.toServiceException(gson)
        }
    }
}

private fun Exception.toServiceException(gson: Gson): ServiceException =
        when (this) {
            is HttpException -> {
                if (this.code() == 401 || this.code() == 403) AuthException(localizedMessage) else APIException(toMessage(gson))
            }
            is JsonSyntaxException -> APIException(message)
            is IOException -> NetworkException(localizedMessage)
            // TODO catch only the exception that Service can throw and let the rest
            // "creash" the app, like JobCancelledException
            else -> ServiceException(localizedMessage)
        }

private fun HttpException.toMessage(gson: Gson) =
    response()?.errorBody()?.use {
        extractMessageFromErrorBody(it, gson)
    } ?: localizedMessage

private fun extractMessageFromErrorBody(responseBody: ResponseBody, gson: Gson): String? =
    if (responseBody.contentType()?.type == "application" && responseBody.contentType()?.subtype == "json") {
        try {
            gson.fromJson(responseBody.string(), ErrorResponse::class.java)
                ?.errors
                ?.mapNotNull { it.message }
                ?.joinToString()
        } catch (e: JsonSyntaxException) {
            null
        }
    } else {
        null
    }

private fun FeedItem.toTransaction(decimalPlaces: Int): Transaction {
    val txAmount = amount?.minorUnits
        ?.let { BigDecimal(it.toBigInteger(), decimalPlaces) }
        ?.let { if (direction == "OUT") it.negate() else it }
        ?: BigDecimal.ZERO

    val txStatus = if (status == "SETTLED") Status.SETTLED else Status.UNSETTLED
    val txSource = if (source == "INTERNAL_TRANSFER") Source.INTERNAL else Source.EXTERNAL

    return Transaction(
        id = feedItemUid ?: "",
        amount = txAmount,
        status = txStatus,
        source = txSource
    )
}

private fun SavingsGoalV2.toSavingGoal(): SavingsGoal? =
    savingsGoalUid?.let {
        SavingsGoal(
            it,
            name ?: it
        )
    }