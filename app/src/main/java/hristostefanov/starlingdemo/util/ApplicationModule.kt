package hristostefanov.starlingdemo.util

import android.app.Application
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import hristostefanov.starlingdemo.presentation.Navigation
import hristostefanov.starlingdemo.presentation.dependences.AmountFormatter
import hristostefanov.starlingdemo.presentation.dependences.TokenStore
import kotlinx.coroutines.channels.Channel
import org.greenrobot.eventbus.EventBus
import java.time.ZoneId
import java.util.*

@Module(subcomponents = [SessionComponent::class])
abstract class ApplicationModule {

    companion object {
        @ApplicationScope
        @Provides
        fun provideEventBus() = EventBus.builder().addIndex(EventBusIndex()).build()

        @ApplicationScope
        @Provides @NavigationChannel
        fun provideNavigationChannel() = Channel<Navigation>()

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

        @ApplicationScope
        @Provides
        fun provideGson() = Gson()
    }

    @ApplicationScope
    @Binds
    abstract fun bindAmountFormatter(amountFormatter: AmountFormatterImpl): AmountFormatter
}