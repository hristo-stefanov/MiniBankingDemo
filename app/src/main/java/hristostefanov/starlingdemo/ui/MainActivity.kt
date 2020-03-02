package hristostefanov.starlingdemo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import hristostefanov.starlingdemo.R

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
}
