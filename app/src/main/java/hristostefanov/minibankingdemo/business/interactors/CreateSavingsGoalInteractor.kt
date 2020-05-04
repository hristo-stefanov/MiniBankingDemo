package hristostefanov.minibankingdemo.business.interactors

import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import org.greenrobot.eventbus.EventBus
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject

class CreateSavingsGoalInteractor @Inject constructor(
    private val repository: Repository,
    private val eventBus: EventBus
) {
    @Throws(ServiceException::class)
    suspend fun execute(name: String, accountId: String, currency: Currency) {
        if (!validateName(name))
            throw IllegalArgumentException()
        repository.createSavingsGoal(name, accountId, currency)
        eventBus.post(DataSourceChangedEvent())
    }

    fun validateName(name: String) = name.isNotBlank()
}