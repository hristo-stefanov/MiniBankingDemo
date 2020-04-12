package hristostefanov.starlingdemo.util

import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import hristostefanov.starlingdemo.App
import hristostefanov.starlingdemo.presentation.dependences.AmountFormatter
import hristostefanov.starlingdemo.presentation.dependences.TokenStore
import org.greenrobot.eventbus.EventBus
import java.time.ZoneId
import java.util.*

@Module(subcomponents = [SessionComponent::class])
abstract class ApplicationModule {

    companion object {
        @ApplicationScope
        @Provides
        fun provideEventBus() = EventBus()

        @Provides
        fun provideLocale(): Locale = Locale.getDefault()

        @Provides
        fun provideZoneId(): ZoneId = ZoneId.systemDefault()

        @Provides
        fun provideStringSupplier(): StringSupplier {
            // the provided implementation references the application context which is always
            // present during the life of the app process, hence no worries about leaks here
            return object : StringSupplier {
                // TODO provide Application to the graph with @BindsInstance
                override fun get(resId: Int): String = App.instance.getString(resId)
            }
        }

        @Provides
        @ApplicationScope
        fun provideTokenStore(): TokenStore {
            // TODO provide Application to the graph with @BindsInstance
            return TokenStoreImpl(App.instance)
        }

        @ApplicationScope
        @Provides
        fun provideGson() = Gson()
    }

    @ApplicationScope
    @Binds
    abstract fun bindAmountFormatter(amountFormatter: AmountFormatterImpl): AmountFormatter
}