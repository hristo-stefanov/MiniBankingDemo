package hristostefanov.minibankingdemo.usecase.output

import arrow.core.Either

interface EnsureLoginCredentialsUI {
    suspend fun promptUserToSubmitCredentials(): Either<Cancel, String>
}

object Cancel
