package hristostefanov.minibankingdemo.util

import android.app.Application
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import hristostefanov.minibankingdemo.presentation.Navigation
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import kotlinx.coroutines.channels.Channel
import org.greenrobot.eventbus.EventBus
import java.time.ZoneId
import java.util.*

@Module(subcomponents = [SessionComponent::class])
abstract class ApplicationModule {

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
        fun provideStringSupplier(application: Application): StringSupplier {
            // the provided implementation references the application context which is always
            // present during the life of the app process, hence no worries about leaks here
            return object : StringSupplier {
                override fun get(resId: Int): String = application.getString(resId)
            }
        }

        @Provides
        @ApplicationScope
        fun provideTokenStore(application: Application): TokenStore {
            return TokenStoreImpl(application)
        }

        @Provides
        fun provideGson() = Gson()
    }

    @Binds
    abstract fun bindAmountFormatter(amountFormatter: AmountFormatterImpl): AmountFormatter
}