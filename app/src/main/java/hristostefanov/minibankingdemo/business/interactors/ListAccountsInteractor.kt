package hristostefanov.minibankingdemo.business.interactors

import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.entities.Account
import javax.inject.Inject

class ListAccountsInteractor @Inject constructor(private val repository: Repository) {
    @Throws(ServiceException::class)
    suspend fun execute(): List<Account> {
        return repository.findAllAccounts()
    }
}