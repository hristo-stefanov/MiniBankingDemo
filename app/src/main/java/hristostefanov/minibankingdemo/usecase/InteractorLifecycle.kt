package hristostefanov.minibankingdemo.usecase

import kotlinx.coroutines.flow.Flow

interface InteractorLifecycle {
    val status: InteractorStatus
    val statusChanged: Flow<InteractorStatus>
}