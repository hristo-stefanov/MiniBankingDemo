package hristostefanov.minibankingdemo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.util.Session
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    internal lateinit var tokenStore: TokenStore

    @Inject
    internal lateinit var session: Session

    override fun onCreate() {
        super.onCreate()

        tokenStore.token?.let { token ->
            session.setToken(token, "Bearer")
        }
    }
}
