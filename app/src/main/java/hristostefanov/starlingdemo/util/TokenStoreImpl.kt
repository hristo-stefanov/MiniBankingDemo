package hristostefanov.starlingdemo.util

import android.content.Context
import android.content.SharedPreferences
import hristostefanov.starlingdemo.presentation.dependences.TokenStore

// TODO secure
class TokenStoreImpl(context: Context): TokenStore {
    private val pref: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    override var token: String?
        get() = pref.getString("token", null)
        set(value) {
            pref.edit().putString("token", value).apply()
        }
}