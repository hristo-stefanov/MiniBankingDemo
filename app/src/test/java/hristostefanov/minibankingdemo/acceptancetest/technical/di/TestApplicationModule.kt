package hristostefanov.minibankingdemo.acceptancetest.technical.di

import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import hristostefanov.minibankingdemo.acceptancetest.businessflow.BusinessRulesTestAutomation
import hristostefanov.minibankingdemo.acceptancetest.businessflow.PresentationTestAutomation
import hristostefanov.minibankingdemo.acceptancetest.technical.*
import hristostefanov.minibankingdemo.presentation.MainCommand
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.util.*
import kotlinx.coroutines.channels.Channel
import org.greenrobot.eventbus.EventBus
import java.time.ZoneId
import java.util.*
import javax.inject.Singleton

@DisableInstallInCheck
@Module(subcomponents = [LoginSessionComponent::class])
abstract class TestApplicationModule {

    companion object {
        @Singleton
        @Provides
        fun provideEventBus(): EventBus = EventBus.builder().addIndex(EventBusIndex()).build()

        @Singleton
        @Provides @MainCommandChannel
        fun provideMainCommandChannel(): Channel<MainCommand> = Channel()

        @Provides
        fun provideLocale(): Locale = Locale.getDefault()

        @Provides
        fun provideZoneId(): ZoneId = ZoneId.systemDefault()

        @Provides
        fun provideStringSupplier(): StringSupplier = object : StringSupplier {
            override fun get(resId: Int): String = ""
        }

        @Provides
        fun provideGson() = Gson()
    }

    @Singleton
    @Binds
    abstract fun bindTokenStore(tokenStore: TokenStoreStub): TokenStore

    @Binds
    abstract fun bindAmountFormatter(amountFormatter: TestAmountFormatter): AmountFormatter

    @Singleton
    @Binds
    abstract fun bind(impl: LoginSessionRegistryImp): LoginSessionRegistry

    @Singleton
    @Binds
    abstract fun bindBusinessRulesTestAutomation(impl: BusinessRulesTestAutomationImpl): BusinessRulesTestAutomation

    @Singleton
    @Binds
    abstract fun bindPresentationTestAutomation(impl: PresentationTestAutomationImpl): PresentationTestAutomation

}