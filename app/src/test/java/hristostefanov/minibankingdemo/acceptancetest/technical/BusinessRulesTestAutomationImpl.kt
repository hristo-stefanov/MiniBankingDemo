package hristostefanov.minibankingdemo.acceptancetest.technical

import hristostefanov.minibankingdemo.acceptancetest.businessflow.BusinessRulesTestAutomation
import hristostefanov.minibankingdemo.business.interactors.CalcRoundUpInteractor
import hristostefanov.minibankingdemo.data.models.*
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class BusinessRulesTestAutomationImpl @Inject constructor(
    private val serviceStub: ServiceStub,
    private val calcRoundUpInteractor: CalcRoundUpInteractor,
): BusinessRulesTestAutomation {
    override fun calculateRoundUp(accountNumber: String): BigDecimal {
        return runBlocking { calcRoundUpInteractor.execute(accountNumber, LocalDate.now()) }
    }

    override fun createAccount(number: String, currency: String, transactions: List<BigDecimal>) {
        val feedItems = mutableListOf<FeedItem>()
        transactions.forEach { amount ->
            val feedItem = FeedItem(
                direction = if (amount.signum() >= 0) "IN" else "OUT",
                amount = CurrencyAndAmount(currency, amount.abs().unscaledValue().toLong()),
                status = "SETTLED"
            )
            feedItems.add(feedItem)
        }
        serviceStub.accounts = serviceStub.accounts + listOf(
            AccountV2(
                accountUid = number, defaultCategory = "", currency = currency
            )
        )
        serviceStub.feedItemsToAccountId = mapOf(number to feedItems)
        serviceStub.accountIdentifierByAccountId += number to AccountIdentifiers(number)
        serviceStub.balancePerAccountMap += number to BalanceV2(effectiveBalance = CurrencyAndAmount(
            currency = currency,
            minorUnits = 0
        ))
    }
}