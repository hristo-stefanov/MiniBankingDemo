package hristostefanov.starlingdemo.business.interactors

import hristostefanov.starlingdemo.business.dependences.Repository
import hristostefanov.starlingdemo.business.dependences.ServiceException
import org.greenrobot.eventbus.EventBus
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject

class CreateSavingsGoalInteractor @Inject constructor(
    private val _repository: Repository,
    private val eventBus: EventBus
) {
    @Throws(ServiceException::class)
    suspend fun execute(name: String, accountId: String, currency: Currency) {
        if (!validateName(name))
            throw IllegalArgumentException()
        _repository.createSavingsGoal(name, accountId, currency)
        eventBus.post(DataSourceChangedEvent())
    }

    fun validateName(name: String) = name.isNotBlank()
}