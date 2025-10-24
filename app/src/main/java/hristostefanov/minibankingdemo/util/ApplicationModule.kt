package hristostefanov.minibankingdemo.util

import android.app.Application
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hristostefanov.minibankingdemo.business.calcStartOfSevenDayWindowIncludingToday
import hristostefanov.minibankingdemo.presentation.Navigation
import hristostefanov.minibankingdemo.presentation.UserInterfaceImpl
import hristostefanov.minibankingdemo.presentation.dependences.AmountFormatter
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.usecase.CalcSincePolicy
import hristostefanov.minibankingdemo.usecase.CreateSavingsGoalInteractor
import hristostefanov.minibankingdemo.usecase.CreateSavingsGoalInteractorImpl
import hristostefanov.minibankingdemo.usecase.subfunction.EnsureLoginCredentialsInteractorImpl
import hristostefanov.minibankingdemo.usecase.GetSummaryInteractorImpl
import hristostefanov.minibankingdemo.usecase.LogoutInteractor
import hristostefanov.minibankingdemo.usecase.LogoutInteractorImpl
import hristostefanov.minibankingdemo.usecase.TransferRoundUpInteractorImpl
import hristostefanov.minibankingdemo.usecase.input.EnsureLoginCredentialsInteractor
import hristostefanov.minibankingdemo.usecase.input.GetSummaryInteractor
import hristostefanov.minibankingdemo.usecase.input.TransferRoundUpInteractor
import hristostefanov.minibankingdemo.usecase.output.EnsureLoginCredentialsUI
import hristostefanov.minibankingdemo.usecase.output.StockUI
import kotlinx.coroutines.channels.Channel
import org.greenrobot.eventbus.EventBus
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.Locale
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(subcomponents = [LoginSessionComponent::class])
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
        fun provideNow(): OffsetDateTime = OffsetDateTime.now()

        @Provides
        fun provideCalcSincePolicy(): CalcSincePolicy = ::calcStartOfSevenDayWindowIncludingToday
    }

    @Binds
    abstract fun bindAmountFormatter(amountFormatter: AmountFormatterImpl): AmountFormatter

    @Singleton
    @Binds
    abstract fun bind(impl: LoginSessionRegistryImp): LoginSessionRegistry

    @Singleton
    @Binds
    abstract fun bindStartup(impl: EnsureLoginCredentialsInteractorImpl): EnsureLoginCredentialsInteractor

    @Singleton
    @Binds
    abstract fun bindLogoutInteractor(impl: LogoutInteractorImpl): LogoutInteractor

    // TODO how to handle recreation of the interactor when triggered again?
    @Singleton
    @Binds
    abstract fun bindGetSummaryInteractor(impl: GetSummaryInteractorImpl): GetSummaryInteractor

    @Singleton
    @Binds
    abstract fun bindTransferRoundUpInteractor(impl: TransferRoundUpInteractorImpl): TransferRoundUpInteractor

    @Singleton
    @Binds
    abstract fun bindStockUI(impl: UserInterfaceImpl): StockUI

    @Singleton
    @Binds
    abstract fun bindEnsureLoginCredentials(impl: UserInterfaceImpl): EnsureLoginCredentialsUI

    @Singleton
    @Binds
    abstract fun bindCreateSavingsGoalInteractor(impl: CreateSavingsGoalInteractorImpl): CreateSavingsGoalInteractor
}