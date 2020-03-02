package hristostefanov.starlingdemo.business.interactors

import hristostefanov.starlingdemo.business.dependences.Repository
import hristostefanov.starlingdemo.business.dependences.ServiceException
import hristostefanov.starlingdemo.business.entities.SavingsGoal
import javax.inject.Inject

class ListSavingGoalsInteractor @Inject constructor(private val repository: Repository) {
    @Throws(ServiceException::class)
    suspend fun execute(accountId: String): List<SavingsGoal> {
        return repository.findSavingGoals(accountId)
    }
}