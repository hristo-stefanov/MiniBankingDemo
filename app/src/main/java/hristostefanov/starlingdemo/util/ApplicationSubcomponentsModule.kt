package hristostefanov.starlingdemo.util

import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import hristostefanov.starlingdemo.App
import hristostefanov.starlingdemo.presentation.dependences.AmountFormatter
import java.time.ZoneId
import java.util.*

@Module(subcomponents = [SessionComponent::class])
abstract class ApplicationSubcomponentsModule {

    companion object {
        @Provides
        fun provideLocale(): Locale = Locale.getDefault()

        @Provides
        fun provideZoneId(): ZoneId = ZoneId.systemDefault()

        @Provides
        fun provideStringSupplier(): StringSupplier {
            // the provided implementation references the application context which is always
            // present during the life of the app process, hence no worries about leaks here
            return object : StringSupplier {
                override fun get(resId: Int): String = App.instance.getString(resId)
            }
        }

        @Provides
        fun provideGson() = Gson()
    }

    @Binds
    abstract fun bindAmountFormatter(amountFormatter: AmountFormatterImpl): AmountFormatter
}