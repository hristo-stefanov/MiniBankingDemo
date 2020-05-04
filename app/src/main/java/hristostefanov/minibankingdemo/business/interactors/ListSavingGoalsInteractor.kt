package hristostefanov.minibankingdemo.business.interactors

import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.entities.SavingsGoal
import javax.inject.Inject

class ListSavingGoalsInteractor @Inject constructor(private val repository: Repository) {
    @Throws(ServiceException::class)
    suspend fun execute(accountId: String): List<SavingsGoal> {
        return repository.findSavingGoals(accountId)
    }
}