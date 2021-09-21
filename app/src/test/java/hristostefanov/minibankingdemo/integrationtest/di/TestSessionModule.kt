package hristostefanov.minibankingdemo.integrationtest.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.integrationtest.ServiceStub
import hristostefanov.minibankingdemo.data.RepositoryImpl
import hristostefanov.minibankingdemo.data.dependences.Service
import hristostefanov.minibankingdemo.integrationtest.TestAutomation
import hristostefanov.minibankingdemo.integrationtest.ServiceStubTestAutomation
import hristostefanov.minibankingdemo.presentation.dependences.TokenStore
import hristostefanov.minibankingdemo.util.SessionScope
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.util.concurrent.TimeUnit

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
                .baseUrl("https://test")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        @SessionScope
        @Provides
        fun provideService(retrofit: Retrofit): ServiceStub {
            val behavior = NetworkBehavior.create().apply {
                setErrorPercent(0)
                setFailurePercent(0)
                setDelay(300, TimeUnit.MICROSECONDS)
            }

            val mockRetrofit = MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build()

            val delegate = mockRetrofit.create(Service::class.java)
            return ServiceStub(delegate)
        }
    }

    // Repositories may cache session specific data, hence the scoping to session
    @SessionScope
    @Binds
    abstract fun bindRepository(repository: RepositoryImpl): Repository

    @SessionScope
    @Binds
    abstract fun bindService(service: ServiceStub): Service

    @SessionScope
    @Binds
    abstract fun bindTestAutomation(impl: ServiceStubTestAutomation): TestAutomation
}