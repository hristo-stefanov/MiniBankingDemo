package hristostefanov.starlingdemo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import hristostefanov.starlingdemo.App
import hristostefanov.starlingdemo.R
import hristostefanov.starlingdemo.presentation.Navigation
import hristostefanov.starlingdemo.util.NavigationChannel
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    @NavigationChannel
    internal lateinit var navigationChannel: Channel<Navigation>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        App.instance.applicationComponent.inject(this)

        // needed to hide the Up button on the ActionBar for top-level destinations
        val topLevelDestinationIds = setOf(R.id.accessTokenDestination, R.id.accountsDestination)

        val navController = findNavController(R.id.navHostFragment)
        appBarConfiguration = AppBarConfiguration(topLevelDestinationIds)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // launch a lifecycle aware coroutine
        lifecycleScope.launchWhenStarted {
            // the terminating condition of the loop is the cancellation of the coroutine
            while (true) {
                val navigation = navigationChannel.receive()
                when (navigation) {
                    is Navigation.Forward -> navController.navigate(navigation.navDirections)
                    is Navigation.Backward -> navController.popBackStack()
                    is Navigation.Restart -> {
                        // this way is better than restarting the Activity which may cause
                        // race condition for consuming the navigation emission
                        navController.navigate(R.id.accountsDestination, null, navOptions {
                            popUpTo(R.id.accountsDestination) {
                                inclusive = true
                            }
                        })
                    }
                    is Navigation.BackTo -> navController.popBackStack(
                        navigation.destinationId,
                        false
                    )
                    is Navigation.Before -> navController.popBackStack(
                        navigation.destinationId,
                        true
                    )
                }
            }
        }
    }

    // To make the Up button operable, we need to override this method.
    // This is not needed when using Toolbar instead of ActionBAr
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
