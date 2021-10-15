package hristostefanov.minibankingdemo.acceptancetest.technical.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import hristostefanov.minibankingdemo.BuildConfig
import hristostefanov.minibankingdemo.acceptancetest.businessflow.BusinessRulesTestAutomation
import hristostefanov.minibankingdemo.acceptancetest.businessflow.PresentationTestAutomation
import hristostefanov.minibankingdemo.acceptancetest.technical.BusinessRulesTestAutomationImpl
import hristostefanov.minibankingdemo.acceptancetest.technical.PresentationTestAutomationImpl
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.interactors.CalcRoundUpInteractor
import hristostefanov.minibankingdemo.business.interactors.CalcRoundUpInteractorImpl
import hristostefanov.minibankingdemo.business.interactors.ListAccountsInteractor
import hristostefanov.minibankingdemo.business.interactors.ListAccountsInteractorImpl
import hristostefanov.minibankingdemo.data.RepositoryImpl
import hristostefanov.minibankingdemo.data.dependences.Service
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.util.MockService
import hristostefanov.minibankingdemo.util.SessionScope
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.util.concurrent.TimeUnit

@DisableInstallInCheck
@Module
abstract class TestSessionModule {

    companion object {
        @SessionScope
        @Provides
        fun provideRetrofit(tokenStore: TokenStore): Retrofit {
            val interceptor = Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${tokenStore.token}").build()
                chain.proceed(request)
            }
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            return Retrofit.Builder()
                .client(client)
                .baseUrl(BuildConfig.SERVICE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        @SessionScope
        @Provides
        fun provideService(retrofit: Retrofit, tokenStore: TokenStore): Service {
            // NOTE: Retrofit coroutines support fulfills the @AnyThread requirement of the Service interface
            if (BuildConfig.SERVICE_BASE_URL == "http:mock") {
                val behavior = NetworkBehavior.create().apply {
                    setErrorPercent(0)
                    setFailurePercent(0)
                    setDelay(300, TimeUnit.MICROSECONDS)
                }

                val mockRetrofit = MockRetrofit.Builder(retrofit)
                    .networkBehavior(behavior)
                    .build()

                val delegate = mockRetrofit.create(Service::class.java)
                return MockService(delegate)
            } else {
                return retrofit.create(Service::class.java)
            }
        }
    }



//     Repositories may cache session specific data, hence the scoping to session
    @SessionScope
    @Binds
    abstract fun bindRepository(repository: RepositoryImpl): Repository

    @SessionScope
    @Binds
    abstract fun bindCalcRoundupInteractor(impl: CalcRoundUpInteractorImpl): CalcRoundUpInteractor

    @SessionScope
    @Binds
    abstract fun bindListAccountsInteractor(impl: ListAccountsInteractorImpl): ListAccountsInteractor
}