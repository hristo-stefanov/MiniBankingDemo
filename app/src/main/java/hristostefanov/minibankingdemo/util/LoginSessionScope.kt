package hristostefanov.minibankingdemo.util

import javax.inject.Scope
import java.lang.annotation.Documented
import kotlin.annotation.AnnotationRetention.RUNTIME

@Scope
@Documented
@Retention(RUNTIME)
annotation class LoginSessionScope