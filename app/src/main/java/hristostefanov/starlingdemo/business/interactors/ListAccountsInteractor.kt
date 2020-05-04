package hristostefanov.starlingdemo.business.interactors

import hristostefanov.starlingdemo.business.dependences.Repository
import hristostefanov.starlingdemo.business.dependences.ServiceException
import hristostefanov.starlingdemo.business.entities.Account
import javax.inject.Inject

class ListAccountsInteractor @Inject constructor(private val repository: Repository) {
    @Throws(ServiceException::class)
    suspend fun execute(): List<Account> {
        return repository.findAllAccounts()
    }
}