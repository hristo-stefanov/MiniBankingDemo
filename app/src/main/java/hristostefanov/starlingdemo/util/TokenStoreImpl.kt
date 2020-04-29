package hristostefanov.starlingdemo.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import hristostefanov.starlingdemo.presentation.dependences.TokenStore

class TokenStoreImpl(context: Context): TokenStore {

    private val pref: SharedPreferences by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create("securePrefs", masterKeyAlias, context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }

    override var token: String
        get() = pref.getString("token", null) ?: ""
        set(value) {
            pref.edit().putString("token", value).apply()
        }
}