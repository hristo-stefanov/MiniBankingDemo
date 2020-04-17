package hristostefanov.starlingdemo.util

import dagger.Component
import hristostefanov.starlingdemo.ui.MainActivity
import org.greenrobot.eventbus.EventBus

@ApplicationScope
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun getSessionComponentFactory(): SessionComponent.Factory
    fun getEventBus(): EventBus

    fun inject(activity: MainActivity)
}