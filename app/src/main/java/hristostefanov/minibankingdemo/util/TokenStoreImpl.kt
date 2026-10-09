package hristostefanov.minibankingdemo.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.core.content.edit
import kotlinx.coroutines.flow.asStateFlow

class TokenStoreImpl(context: Context): TokenStore {
    private var _token: String? = null
    override val token get() = _token

    private val pref: SharedPreferences by lazy {
        // TODO reconsider this
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create("securePrefs", masterKeyAlias, context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }

    init {
        _token = pref.getString("token", null)
    }

    override fun setToken(token: String?) {
        pref.edit { putString("token", token) }
        _token = token
    }
}