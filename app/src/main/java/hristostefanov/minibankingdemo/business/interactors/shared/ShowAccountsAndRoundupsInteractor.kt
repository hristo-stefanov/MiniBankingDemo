package hristostefanov.minibankingdemo.business.interactors.shared

import hristostefanov.minibankingdemo.business.interactors.CalcRoundUpInteractor
import java.time.LocalDate
import javax.inject.Inject

class ShowAccountsAndRoundupsInteractor @Inject constructor(
//    val calcRoundUpInteractor: CalcRoundUpInteractor,
    val output: ShowAccountsAndRoundupsOutputBoundary
) {
    fun execute() {
//        val roundUpSinceDate: LocalDate = LocalDate.now().minusWeeks(1)
//        calcRoundUpInteractor.execute("1", roundUpSinceDate)


        // TODO calculate
        val model = ShowAccountsAndRoundupModel()
        output.showReport(model)
    }
}