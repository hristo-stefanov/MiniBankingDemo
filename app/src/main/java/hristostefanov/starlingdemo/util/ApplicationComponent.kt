package hristostefanov.starlingdemo.util

import dagger.Component

@ApplicationScope
@Component(modules = [ApplicationSubcomponentsModule::class])
interface ApplicationComponent {
    fun getSessionComponentFactory(): SessionComponent.Factory
}