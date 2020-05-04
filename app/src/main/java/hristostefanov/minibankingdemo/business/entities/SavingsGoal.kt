package hristostefanov.minibankingdemo.business.entities

import java.io.Serializable

data class SavingsGoal(
    val id: String,
    val name: String
) : Serializable