package hristostefanov.starlingdemo.util

import dagger.Component
import org.greenrobot.eventbus.EventBus

@ApplicationScope
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun getSessionComponentFactory(): SessionComponent.Factory
    fun getEventBus(): EventBus
}