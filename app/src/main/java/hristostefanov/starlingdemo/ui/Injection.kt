package hristostefanov.starlingdemo.ui

import androidx.fragment.app.Fragment
import hristostefanov.starlingdemo.App

fun Fragment.sessionComponent() = (requireContext().applicationContext as App).sessionComponent
