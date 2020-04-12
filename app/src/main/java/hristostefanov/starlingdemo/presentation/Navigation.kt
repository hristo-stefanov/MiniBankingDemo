package hristostefanov.starlingdemo.presentation

import androidx.annotation.IdRes
import androidx.navigation.NavDirections

sealed class Navigation {
    object Backward : Navigation()
    class BackTo(@IdRes val destinationId: Int) : Navigation()
    class Before(@IdRes val destinationId: Int): Navigation()
    data class Forward(val navDirections: NavDirections) : Navigation()
}