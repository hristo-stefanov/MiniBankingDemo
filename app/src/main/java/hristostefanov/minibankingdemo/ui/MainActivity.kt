package hristostefanov.minibankingdemo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hristostefanov.minibankingdemo.R
import hristostefanov.minibankingdemo.presentation.MainCommand
import hristostefanov.minibankingdemo.util.MainCommandChannel
import io.sentry.android.navigation.SentryNavigationListener
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        var isFreshProcess = true
    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    @MainCommandChannel
    internal lateinit var mainCommandChannel: Channel<MainCommand>

    private val navController by lazy { findNavController(R.id.navHostFragment) }

    private val sentryNavListener = SentryNavigationListener(
        enableNavigationBreadcrumbs = true,
        enableNavigationTracing = true,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isProcessDeathRestore = savedInstanceState != null && isFreshProcess
        isFreshProcess = false

        if (isProcessDeathRestore) {
            // Reset the navigation state since we don't restore full app state after process death
            navController.setGraph(R.navigation.nav_graph)
        }

        // needed to hide the Up button on the ActionBar for top-level destinations
        val topLevelDestinationIds = setOf(R.id.loginDestination, R.id.accountsDestination)

        appBarConfiguration = AppBarConfiguration(topLevelDestinationIds)
        setupActionBarWithNavController(navController, appBarConfiguration)

        mainCommandChannel
            .receiveAsFlow()
            .flowWithLifecycle(lifecycle)
            .onEach { navigation ->
                onMainCommand(navigation, navController)
            }
            .launchIn(lifecycleScope)
    }


    private fun onMainCommand(mainCommand: MainCommand, navController: NavController) {
        when (mainCommand) {
            is MainCommand.NavigateForward -> navController.navigate(mainCommand.navDirections)
            is MainCommand.NavigateForwardToDestination -> navController.navigate(mainCommand
                .destinationResId, mainCommand.args, mainCommand.navOptions)
            is MainCommand.NavigateBackward -> navController.popBackStack()
            is MainCommand.Restart -> {
                // this way is better than restarting the Activity which may cause
                // race condition for consuming the navigation emission
                navController.navigate(R.id.accountsDestination, null, navOptions {
                    popUpTo(R.id.accountsDestination) {
                        inclusive = true
                    }
                })
            }
            is MainCommand.NavigateBackTo -> navController.popBackStack(
                mainCommand.destinationId,
                false
            )
            is MainCommand.NavigateBefore -> navController.popBackStack(
                mainCommand.destinationId,
                true
            )
            is MainCommand.ShowSnackbar -> {
                val view = findViewById<ConstraintLayout>(R.id.rootLayout)
                Snackbar.make(view, mainCommand.message, Snackbar.LENGTH_LONG).show()
            }
            is MainCommand.ShowConfirmationDialog -> {
                with(mainCommand) {
                    ConfirmationDialog.create(title = title, message = message, isCancelable = isCancelable)
                }.show(supportFragmentManager, null)
            }
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
