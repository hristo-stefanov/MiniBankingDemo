package hristostefanov.starlingdemo

import android.app.Application
import hristostefanov.starlingdemo.util.ApplicationComponent
import hristostefanov.starlingdemo.util.DaggerApplicationComponent

class App : Application() {
    val applicationComponent: ApplicationComponent =
        DaggerApplicationComponent.factory().create(this)
}