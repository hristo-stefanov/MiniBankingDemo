package hristostefanov.minibankingdemo.util

import dagger.BindsInstance
import dagger.Subcomponent
import hristostefanov.minibankingdemo.business.interactors.*

// NOTE: Another option would be to use a Hilt's "custom component" which is essentially
// a subcomponent but with less code and with some limitations. See
// https://medium.com/androiddevelopers/hilt-adding-components-to-the-hierarchy-96f207d6d92d
// https://dagger.dev/hilt/custom-components
@SessionScope
@Subcomponent(modules = [SessionModule::class])
interface SessionComponent {
    @Subcomponent.Factory
    interface Factory {
        // TODO make it named
        fun create(@BindsInstance token: String): SessionComponent
    }

    val token: String

    val calcRoundUpInteractor: CalcRoundUpInteractor
    val listAccountsInteractor: ListAccountsInteractor
    val listSavingGoalInteractor: ListSavingGoalsInteractor
    val addMoneyIntoGoalInteractor: AddMoneyIntoGoalInteractor
    val createSavingGoalsInteractor: CreateSavingsGoalInteractor
}