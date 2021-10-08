package hristostefanov.minibankingdemo.acceptancetest.technical.di

import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import hristostefanov.minibankingdemo.acceptancetest.technical.TestAmountFormatter
import hristostefanov.minibankingdemo.acceptancetest.technical.TokenStoreStub
import hristostefanov.minibankingdemo.presentation.Navigation
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.util.*
import kotlinx.coroutines.channels.Channel
import org.greenrobot.eventbus.EventBus
import java.time.ZoneId
import java.util.*

@Module(subcomponents = [TestSessionComponent::class])
abstract class TestApplicationModule {

    companion object {
        @ApplicationScope
        @Provides
        fun provideEventBus(): EventBus = EventBus.builder().addIndex(EventBusIndex()).build()

        @ApplicationScope
        @Provides @NavigationChannel
        fun provideNavigationChannel(): Channel<Navigation> = Channel()

        @Provides
        fun provideLocale(): Locale = Locale.getDefault()

        @Provides
        fun provideZoneId(): ZoneId = ZoneId.systemDefault()

        @Provides
        fun provideStringSupplier(): StringSupplier = object : StringSupplier {
            override fun get(resId: Int): String = ""
        }
    }

    @ApplicationScope
    @Binds
    abstract fun bindTokenStore(tokenStore: TokenStoreStub): TokenStore

    @Binds
    abstract fun bindAmountFormatter(amountFormatter: TestAmountFormatter): AmountFormatter
}