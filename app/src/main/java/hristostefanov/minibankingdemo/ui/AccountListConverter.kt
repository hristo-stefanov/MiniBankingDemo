package hristostefanov.minibankingdemo.ui

import hristostefanov.minibankingdemo.presentation.DisplayAccount

object AccountListConverter {
    @JvmStatic
    fun toAdapter(list: List<DisplayAccount>?) = list?.let {AccountListAdapter(it)}
}