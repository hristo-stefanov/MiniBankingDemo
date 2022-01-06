package hristostefanov.minibankingdemo.util

import dagger.BindsInstance
import dagger.Subcomponent
import hristostefanov.minibankingdemo.business.interactors.*
// TODO rename Session* to LoginSession* to make it clear that the lifecycle is the
// as the one of the interactive user session

// NOTE: Another option would be to use a Hilt's "custom component" which is essentially
// a subcomponent but with less code and with some limitations. See
// https://medium.com/androiddevelopers/hilt-adding-components-to-the-hierarchy-96f207d6d92d
// https://dagger.dev/hilt/custom-components
@SessionScope
@Subcomponent(modules = [SessionModule::class])
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

    val calcRoundUpInteractor: CalcRoundUpInteractor
    val listAccountsInteractor: ListAccountsInteractor
    val listSavingGoalInteractor: ListSavingGoalsInteractor
    val addMoneyIntoGoalInteractor: AddMoneyIntoGoalInteractor
    val createSavingGoalsInteractor: CreateSavingsGoalInteractor
}