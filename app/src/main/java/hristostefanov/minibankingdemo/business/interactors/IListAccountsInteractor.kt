package hristostefanov.minibankingdemo.business.interactors

import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.entities.Account

interface IListAccountsInteractor {
    @Throws(ServiceException::class)
    suspend fun execute(): List<Account>
}