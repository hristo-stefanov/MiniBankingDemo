package hristostefanov.starlingdemo.util

import dagger.Binds
import dagger.Module
import dagger.Provides
import hristostefanov.starlingdemo.BuildConfig
import hristostefanov.starlingdemo.business.dependences.Repository
import hristostefanov.starlingdemo.data.RepositoryImpl
import hristostefanov.starlingdemo.data.dependences.Service
import hristostefanov.starlingdemo.presentation.SharedState
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.util.concurrent.TimeUnit

@Module
abstract class SessionModule {

    companion object {
        // Do not scope to allow changing the access token
        @Provides
        fun provideRetrofit(sharedState: SharedState): Retrofit {
            val interceptor = Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${sharedState.accessToken}").build()
                chain.proceed(request)
            }
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            return Retrofit.Builder()
                .client(client)
                .baseUrl(BuildConfig.SERVICE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        // Do not scope to allow switching between mock and real service
        @Provides
        fun provideService(retrofit: Retrofit, sharedState: SharedState): Service  {
            if (sharedState.isMockService) {
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

    @Binds
    abstract fun bindRepository(repository: RepositoryImpl): Repository

}