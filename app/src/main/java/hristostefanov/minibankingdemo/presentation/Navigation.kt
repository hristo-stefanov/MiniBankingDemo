package hristostefanov.minibankingdemo.presentation

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions

sealed class Navigation {
    data object Backward : Navigation()
    data object Restart: Navigation()
    class BackTo(@IdRes val destinationId: Int) : Navigation()
    class Before(@IdRes val destinationId: Int): Navigation()
    // This is used with nav actions only
    data class Forward(val navDirections: NavDirections) : Navigation()
    data class ForwardToDestination(
        @IdRes val destinationResId: Int,
        var args: Bundle? = null,
        val
        navOptions: NavOptions? = null
    ): Navigation()
    data class Message(val message: String): Navigation()
}