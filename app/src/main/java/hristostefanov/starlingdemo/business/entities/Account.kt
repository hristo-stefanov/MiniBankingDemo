package hristostefanov.starlingdemo.business.entities

import java.math.BigDecimal
import java.util.*

data class Account(
    val id: String,
    val accountNum: String,
    val categoryUid: String,
    val currency: Currency,
    val balance: BigDecimal
)