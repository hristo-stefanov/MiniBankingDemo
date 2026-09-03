package hristostefanov.minibankingdemo.util

import dagger.BindsInstance
import dagger.Subcomponent
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.interactors.*
import hristostefanov.minibankingdemo.usecase.output.ViewSummaryUI

// NOTE: Another option would be to use a Hilt's "custom component" which is essentially
// a subcomponent but with less code and with some limitations. See
// https://medium.com/androiddevelopers/hilt-adding-components-to-the-hierarchy-96f207d6d92d
// https://dagger.dev/hilt/custom-components
@LoginSessionScope
@Subcomponent(modules = [LoginSessionModule::class])
interface LoginSessionComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(
            @AccessToken
            @BindsInstance
            token: String,
            @TokenType
            @BindsInstance
            tokenType: String
        ): LoginSessionComponent
    }

    // used for testing automation
    @get:AccessToken
    val accessToken: String

    val addMoneyIntoGoalInteractor: AddMoneyIntoGoalInteractor
    val repository: Repository
}