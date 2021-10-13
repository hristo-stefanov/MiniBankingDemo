package hristostefanov.minibankingdemo.util

import dagger.Subcomponent
import hristostefanov.minibankingdemo.business.interactors.*
import hristostefanov.minibankingdemo.presentation.*

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