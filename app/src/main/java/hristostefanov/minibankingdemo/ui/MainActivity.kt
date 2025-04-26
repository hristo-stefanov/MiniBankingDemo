package hristostefanov.minibankingdemo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.presentation.Navigation
import hristostefanov.minibankingdemo.util.NavigationChannel
import io.sentry.android.navigation.SentryNavigationListener
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    @NavigationChannel
    internal lateinit var navigationChannel: Channel<Navigation>

    private val navController by lazy { findNavController(R.id.navHostFragment) }

    private val sentryNavListener = SentryNavigationListener(
        enableNavigationBreadcrumbs = true,
        enableNavigationTracing = true,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // needed to hide the Up button on the ActionBar for top-level destinations
        val topLevelDestinationIds = setOf(R.id.loginDestination, R.id.accountsDestination)

        appBarConfiguration = AppBarConfiguration(topLevelDestinationIds)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navigationChannel
            .receiveAsFlow()
            .flowWithLifecycle(lifecycle)
            .onEach { navigation ->
                onNavigation(navigation, navController)
            }
            .launchIn(lifecycleScope)
    }

    private fun onNavigation(navigation: Navigation, navController: NavController) {
        when (navigation) {
            is Navigation.Forward -> navController.navigate(navigation.navDirections)
            is Navigation.ForwardToDestination -> navController.navigate(navigation
                .destinationResId, navigation.args, navigation.navOptions)
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

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(sentryNavListener)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(sentryNavListener)
    }


    // To make the Up button operable, we need to override this method.
    // This is not needed when using Toolbar instead of ActionBAr
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
