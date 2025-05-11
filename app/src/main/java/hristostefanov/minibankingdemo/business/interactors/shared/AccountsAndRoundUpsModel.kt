package hristostefanov.minibankingdemo.business.interactors.shared

import java.math.BigDecimal
import java.util.Currency

data class AccountsAndRoundUpsModel(
    val items: List<Item>
) {
    data class Item(
        val accountId: String,
        val number: String,
        val currency: Currency,
        val roundUp: BigDecimal,
        val balance: BigDecimal,
    )
}