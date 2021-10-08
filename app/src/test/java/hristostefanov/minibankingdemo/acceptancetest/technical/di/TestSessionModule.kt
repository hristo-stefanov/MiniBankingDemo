package hristostefanov.minibankingdemo.acceptancetest.technical.di

import dagger.Binds
import dagger.Module
import hristostefanov.minibankingdemo.acceptancetest.businessflow.BusinessRulesTestAutomation
import hristostefanov.minibankingdemo.acceptancetest.businessflow.PresentationTestAutomation
import hristostefanov.minibankingdemo.acceptancetest.technical.BusinessRulesTestAutomationImpl
import hristostefanov.minibankingdemo.acceptancetest.technical.PresentationTestAutomationImpl
import hristostefanov.minibankingdemo.util.SessionScope

@Module
abstract class TestSessionModule {
    @SessionScope
    @Binds
    abstract fun bindBusinessRulesTestAutomation(impl: BusinessRulesTestAutomationImpl): BusinessRulesTestAutomation

    @SessionScope
    @Binds
    abstract fun bindPresentationTestAutomation(impl: PresentationTestAutomationImpl): PresentationTestAutomation
}