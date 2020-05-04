package hristostefanov.minibankingdemo.data.models

import java.math.BigDecimal

data class FeedItem(
    val feedItemUid: String? = null,
    val categoryUid: String? = null,
    val amount: CurrencyAndAmount? = null,
    val sourceAmount: CurrencyAndAmount? = null,
    val direction: String? = null,
    val updatedAt: String? = null,
    val transactionTime: String? = null,
    val settlementTime: String? = null,
    val retryAllocationUntilTime: String? = null,
    val source: String? = null,
    val sourceSubType: String? = null,
    val status: String? = null,
    val counterPartyType: String? = null,
    val counterPartyUid: String? = null,
    val counterPartyName: String? = null,
    val counterPartySubEntityUid: String? = null,
    val counterPartySubEntityName: String? = null,
    val counterPartySubEntityIdentifier: String? = null,
    val counterPartySubEntitySubIdentifier: String? = null,
    val exchangeRate: BigDecimal? = null,
    val totalFees: BigDecimal? = null,
    val reference: String? = null,
    val country: String? = null,
    val spendingCategory: String? = null,
    val userNote: String? = null,
    val roundUp: RoundUpDetailsAssociatedWithFeedItem?  = null
)
