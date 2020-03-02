package hristostefanov.starlingdemo.util

import androidx.annotation.StringRes

interface StringSupplier {
    fun get(@StringRes resId: Int): String
}