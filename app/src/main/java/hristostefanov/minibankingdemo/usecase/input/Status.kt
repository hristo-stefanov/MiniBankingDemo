package hristostefanov.minibankingdemo.usecase.input

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import hristostefanov.minibankingdemo.usecase.input.Termination.*

// NOTE: inlining functions is needed to allow for both suspend and non-suspend argument

typealias Status = Either<Termination, Completion>

sealed interface Termination {
    data class Failure(val exception: Throwable) : Termination
    data object Cancellation : Termination
}

object Completion

fun Termination.status() = this.left()

fun Completion.status() = this.right()

inline fun Status.onCompletion(block: (Completion) -> Unit) = this.onRight(block)

inline fun Status.onTermination(block: (Termination) -> Unit) = this.onLeft(block)

/**
 * This function wraps [arrow.core.Either.fold] with a [Status] related signature.
 */
inline fun <C> Status.fold(
    ifTermination: (left: Termination) -> C,
    ifCompletion: (right: Completion) -> C
) = this.fold(ifTermination, ifCompletion)

fun Status.isFailure() = this.isLeft { it is Failure }
fun Status.isCancellation() = this.isLeft { it is Cancellation }

