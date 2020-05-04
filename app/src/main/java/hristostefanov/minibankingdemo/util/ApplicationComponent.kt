package hristostefanov.minibankingdemo.util

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import hristostefanov.minibankingdemo.ui.MainActivity

@ApplicationScope
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: Application): ApplicationComponent
    }

    val sessionRegistry: SessionRegistry
    fun inject(target: MainActivity)
}