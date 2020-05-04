package hristostefanov.minibankingdemo

import android.app.Application
import hristostefanov.minibankingdemo.util.ApplicationComponent
import hristostefanov.minibankingdemo.util.DaggerApplicationComponent

class App : Application() {
    val applicationComponent: ApplicationComponent =
        DaggerApplicationComponent.factory().create(this)
}