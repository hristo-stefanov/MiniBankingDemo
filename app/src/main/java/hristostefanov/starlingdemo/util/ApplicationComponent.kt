package hristostefanov.starlingdemo.util

import dagger.Component

@ApplicationScope
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun getSessionComponentFactory(): SessionComponent.Factory
}