package hristostefanov.starlingdemo.ui

import hristostefanov.starlingdemo.presentation.DisplayAccount

object AccountListConverter {
    @JvmStatic
    fun toAdapter(list: List<DisplayAccount>?) = list?.let {AccountListAdapter(it)}
}