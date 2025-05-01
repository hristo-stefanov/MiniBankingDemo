package hristostefanov.minibankingdemo.business.interactors.shared

import java.math.BigDecimal

data class ShowAccountsAndRoundupModel(
    val items: List<Item>
) {
    data class Item(
        val accountId: String,
        val number: String,
        val roundUp: BigDecimal,
        val balance: BigDecimal,
    )
}