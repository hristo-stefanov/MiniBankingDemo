package hristostefanov.minibankingdemo.util

import dagger.Subcomponent
import hristostefanov.minibankingdemo.business.interactors.*
import hristostefanov.minibankingdemo.presentation.*

// NOTE: Another option would be to use a Hilt's "custom component" which is essentially
// a subcomponent but with less code and with some limitations. See
// https://medium.com/androiddevelopers/hilt-adding-components-to-the-hierarchy-96f207d6d92d
// https://dagger.dev/hilt/custom-components
@SessionScope
@Subcomponent(modules = [SessionModule::class])
interface SessionComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SessionComponent
    }

    val calcRoundUpInteractor: CalcRoundUpInteractor
    val listAccountsInteractor: ListAccountsInteractor
    val listSavingGoalInteractor: ListSavingGoalsInteractor
    val addMoneyIntoGoalInteractor: AddMoneyIntoGoalInteractor
    val createSavingGoalsInteractor: CreateSavingsGoalInteractor
}