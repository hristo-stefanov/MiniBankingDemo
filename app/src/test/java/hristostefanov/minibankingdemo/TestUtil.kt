package hristostefanov.minibankingdemo

import org.mockito.Mockito

// There are some problems with using Mockito with Kotlin as described:
// https://discuss.kotlinlang.org/t/how-to-use-mockito-with-kotlin/324
// https://stackoverflow.com/questions/30305217/is-it-possible-to-use-mockito-in-kotlin
// hence the workaround functions:

fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

fun <T> any(): T {
    return Mockito.any<T>()
}

fun <T> eq(t: T): T = Mockito.eq<T>(t)
fun <T> uninitialized(): T = null as T