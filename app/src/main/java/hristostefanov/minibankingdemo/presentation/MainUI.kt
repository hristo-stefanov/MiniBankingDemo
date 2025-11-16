package hristostefanov.minibankingdemo.presentation

import hristostefanov.minibankingdemo.usecase.input.Status

interface MainUI {
    suspend fun presentStatus(status: Status)
    suspend fun presentMessage(message: String)
}