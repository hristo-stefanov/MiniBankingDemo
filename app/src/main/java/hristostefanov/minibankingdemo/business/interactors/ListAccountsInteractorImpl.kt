package hristostefanov.minibankingdemo.business.interactors

import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.entities.Account
import javax.inject.Inject

class ListAccountsInteractorImpl @Inject constructor(private val repository: Repository) :
    ListAccountsInteractor {
    @Throws(ServiceException::class)
    override suspend fun execute(): List<Account> {
        return repository.findAllAccounts()
    }
}