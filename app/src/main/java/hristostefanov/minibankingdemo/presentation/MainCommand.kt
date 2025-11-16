package hristostefanov.minibankingdemo.presentation

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions

sealed class MainCommand {
    data object NavigateBackward : MainCommand()
    data object Restart: MainCommand()
    class NavigateBackTo(@IdRes val destinationId: Int) : MainCommand()
    class NavigateBefore(@IdRes val destinationId: Int): MainCommand()
    // This is used with nav actions only
    data class NavigateForward(val navDirections: NavDirections) : MainCommand()
    data class NavigateForwardToDestination(
        @IdRes val destinationResId: Int,
        var args: Bundle? = null,
        val
        navOptions: NavOptions? = null
    ): MainCommand()
    data class ShowSnackbar(val message: String): MainCommand()
}