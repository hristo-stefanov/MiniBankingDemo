package hristostefanov.starlingdemo.util

import dagger.Binds
import dagger.Module
import hristostefanov.starlingdemo.business.dependences.Repository
import hristostefanov.starlingdemo.data.RepositoryImpl
import hristostefanov.starlingdemo.presentation.dependences.AmountFormatter

@Module
abstract class BindingModule {
    @Binds
    abstract fun bindRepository(repository: RepositoryImpl): Repository

    @Binds
    abstract fun bindAmountFormatter(amountFormatter: AmountFormatterImpl): AmountFormatter
}