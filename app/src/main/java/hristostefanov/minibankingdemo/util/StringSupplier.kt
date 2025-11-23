package hristostefanov.minibankingdemo.util

import androidx.annotation.StringRes

interface StringSupplier {
    fun get(@StringRes resId: Int, vararg formatArgs: Any): String
}