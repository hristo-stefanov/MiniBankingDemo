package hristostefanov.starlingdemo

import android.app.Application
import hristostefanov.starlingdemo.util.ApplicationComponent
import hristostefanov.starlingdemo.util.DaggerApplicationComponent
import hristostefanov.starlingdemo.util.SessionComponent

class App : Application() {
    companion object {
        lateinit var instance: App
    }

    lateinit var sessionComponent: SessionComponent

    val applicationComponent: ApplicationComponent = DaggerApplicationComponent.create()

    init {
        instance = this
        newSession() // TODO needed when resurrecting the app but not when starting initially
    }

    fun newSession() {
        sessionComponent = applicationComponent.getSessionComponentFactory().create()
    }
}