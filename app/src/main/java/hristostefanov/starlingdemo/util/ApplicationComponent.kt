package hristostefanov.starlingdemo.util

import dagger.Component
import hristostefanov.starlingdemo.ui.MainActivity

@ApplicationScope
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    val sessionRegistry: SessionRegistry
    fun inject(activity: MainActivity)
}