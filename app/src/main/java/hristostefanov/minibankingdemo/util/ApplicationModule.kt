package hristostefanov.minibankingdemo.util

import android.app.Application
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hristostefanov.minibankingdemo.BuildConfig
import hristostefanov.minibankingdemo.presentation.Navigation
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.util.oauth.OAuth
import kotlinx.coroutines.channels.Channel
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.ZoneId
import java.util.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(subcomponents = [SessionComponent::class])
abstract class ApplicationModule {

    companion object {
        @Singleton
        @Provides
        fun provideEventBus(): EventBus = EventBus.builder().addIndex(EventBusIndex()).build()

        @Singleton
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
        @Singleton
        fun provideTokenStore(application: Application): TokenStore {
            return TokenStoreImpl(application)
        }

        @Provides
        fun provideGson() = Gson()

        @Provides
        fun provideOAuth(): OAuth {
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.SERVICE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(OAuth::class.java)
        }
    }

    @Binds
    abstract fun bindAmountFormatter(amountFormatter: AmountFormatterImpl): AmountFormatter

    @Binds
    abstract fun bind(impl: SessionRegistryImp): SessionRegistry
}