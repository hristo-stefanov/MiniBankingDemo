package hristostefanov.starlingdemo.data.models

import java.math.BigDecimal

data class FeedItem(
    val feedItemUid: String?,
    val categoryUid: String?,
    val amount: CurrencyAndAmount?,
    val sourceAmount: CurrencyAndAmount?,
    val direction: String?,
    val updatedAt: String?,
    val transactionTime: String?,
    val settlementTime: String?,
    val retryAllocationUntilTime: String?,
    val source: String?,
    val sourceSubType: String?,
    val status: String?,
    val counterPartyType: String?,
    val counterPartyUid: String?,
    val counterPartyName: String?,
    val counterPartySubEntityUid: String?,
    val counterPartySubEntityName: String?,
    val counterPartySubEntityIdentifier: String?,
    val counterPartySubEntitySubIdentifier: String?,
    val exchangeRate: BigDecimal?,
    val totalFees: BigDecimal?,
    val reference: String?,
    val country: String?,
    val spendingCategory: String?,
    val userNote: String?,
    val roundUp: RoundUpDetailsAssociatedWithFeedItem?


)
