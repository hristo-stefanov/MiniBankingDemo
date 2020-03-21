package hristostefanov.starlingdemo.util

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import hristostefanov.starlingdemo.App
import hristostefanov.starlingdemo.BuildConfig
import hristostefanov.starlingdemo.presentation.SharedState
import hristostefanov.starlingdemo.data.dependences.Service
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class ProvidingModule {
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

    @Provides
    fun provideLocale(): Locale = Locale.getDefault()

    @Provides
    fun provideZoneId(): ZoneId = ZoneId.systemDefault()

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

    @Singleton
    @Provides
    fun provideStringSupplier(): StringSupplier {
        // the provided implementation references the application context which is always
        // present during the life of the app process, hence no worries about leaks here
        return object : StringSupplier {
            override fun get(resId: Int): String = App.instance.getString(resId)
        }
    }

    @Singleton
    @Provides
    fun provideGson() = Gson()
}