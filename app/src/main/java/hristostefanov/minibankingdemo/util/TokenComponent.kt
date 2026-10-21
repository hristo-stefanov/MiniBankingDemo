package hristostefanov.minibankingdemo.util

import dagger.BindsInstance
import dagger.Subcomponent
import hristostefanov.minibankingdemo.business.dependences.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

// NOTE: Another option would be to use a Hilt's "custom component" which is essentially
// a subcomponent but with less code and with some limitations. See
// https://medium.com/androiddevelopers/hilt-adding-components-to-the-hierarchy-96f207d6d92d
// https://dagger.dev/hilt/custom-components
@TokenScope
@Subcomponent(modules = [TokenModule::class])
interface TokenComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(
            @AccessToken
            @BindsInstance
            token: String,
            @TokenType
            @BindsInstance
            tokenType: String
        ): TokenComponent
    }

    // used for testing automation
    @get:AccessToken
    val accessToken: String

    val repository: Repository

    val job: Job

    /**
     * A coroutine that updates [State] belongs to the same lifecycle as that state.
     * Launch coroutines that use [state] in this scope.
     */
    val scope: CoroutineScope
}