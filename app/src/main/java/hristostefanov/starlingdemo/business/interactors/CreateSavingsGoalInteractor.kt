package hristostefanov.starlingdemo.business.interactors

import hristostefanov.starlingdemo.business.dependences.Repository
import hristostefanov.starlingdemo.business.dependences.ServiceException
import javax.inject.Inject

class CreateSavingsGoalInteractor @Inject constructor(private val _repository: Repository) {
    @Throws(ServiceException::class)
    suspend fun execute(name: String, accountId: String, currency: String) {
        _repository.createSavingsGoal(name, accountId, currency)
    }
}