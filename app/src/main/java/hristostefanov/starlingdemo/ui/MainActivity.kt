package hristostefanov.starlingdemo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import hristostefanov.starlingdemo.App
import hristostefanov.starlingdemo.R
import hristostefanov.starlingdemo.presentation.Navigation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // needed to hide the Up button on the ActionBar for top-level destinations
        val topLevelDestinationIds = setOf(R.id.accessTokenDestination, R.id.accountsDestination)

        val navController = findNavController(R.id.navHostFragment)
        appBarConfiguration = AppBarConfiguration(topLevelDestinationIds)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    // To make the Up button operable, we need to override this method.
    // This is not needed when using Toolbar instead of ActionBAr
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNavigation(navigation: Navigation) {
        val navController = findNavController(R.id.navHostFragment)
        when (navigation) {
            is Navigation.Forward -> navController.navigate(navigation.navDirections)
            is Navigation.Backward -> navController.popBackStack()
            is Navigation.BackTo -> navController.popBackStack(navigation.destinationId, false)
            is Navigation.Before -> navController.popBackStack(navigation.destinationId, true)
        }
    }

    override fun onStart() {
        super.onStart()
        // TODO consider injection
        App.instance.applicationComponent.getEventBus().register(this)
    }

    override fun onStop() {
        App.instance.applicationComponent.getEventBus().unregister(this)
        super.onStop()
    }
}
