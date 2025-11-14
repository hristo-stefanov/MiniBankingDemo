package hristostefanov.minibankingdemo.presentation

import hristostefanov.minibankingdemo.usecase.input.Status

interface StatusUI {
    suspend fun presentStatus(status: Status)
}