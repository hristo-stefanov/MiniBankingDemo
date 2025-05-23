package hristostefanov.minibankingdemo.acceptancetest.businessflow

import hristostefanov.minibankingdemo.any
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.entities.Account
import hristostefanov.minibankingdemo.business.entities.Transaction
import hristostefanov.minibankingdemo.usecase.AccountsAndRoundUpsModel
import hristostefanov.minibankingdemo.usecase.PresentAccountsAndRoundUpsOutputBoundary
import hristostefanov.minibankingdemo.usecase.PresentAccountsAndRoundupsInteractor
import io.cucumber.java.DataTableType
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.sentry.Breadcrumb.transaction
import kotlinx.coroutines.test.runTest
import org.mockito.AdditionalAnswers.answer
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Currency

