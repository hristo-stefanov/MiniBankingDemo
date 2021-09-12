package hristostefanov.minibankingdemo.integrationtest.di

import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import hristostefanov.minibankingdemo.integrationtest.TokenStoreStub
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.util.ApplicationScope
import java.time.ZoneId
import java.util.*

@Module(subcomponents = [TestSessionComponent::class])
abstract class TestApplicationModule {

    companion object {
        @Provides
        fun provideLocale(): Locale = Locale.getDefault()

        @Provides
        fun provideZoneId(): ZoneId = ZoneId.systemDefault()

        @Provides
        fun provideGson() = Gson()
    }

    @ApplicationScope
    @Binds
    abstract fun bindTokenStore(tokenStore: TokenStoreStub): TokenStore
}