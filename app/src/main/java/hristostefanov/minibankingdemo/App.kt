package hristostefanov.minibankingdemo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    internal lateinit var tokenStore: TokenStore

    @Inject
    internal lateinit var sessionRegistry: LoginSessionRegistry

    override fun onCreate() {
        super.onCreate()

        // This is executed regardless of whether the app is restoring from process death
        // or starting fresh
        if (tokenStore.token.isNotEmpty()) {
            sessionRegistry.createSession(tokenStore.token, "Bearer")
        }
    }
}