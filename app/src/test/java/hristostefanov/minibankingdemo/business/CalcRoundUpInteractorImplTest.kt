package hristostefanov.minibankingdemo.business

import hristostefanov.minibankingdemo.any
import hristostefanov.minibankingdemo.business.dependences.Repository
import hristostefanov.minibankingdemo.business.entities.Source.EXTERNAL
import hristostefanov.minibankingdemo.business.entities.Source.INTERNAL
import hristostefanov.minibankingdemo.business.entities.Status.SETTLED
import hristostefanov.minibankingdemo.business.entities.Status.UNSETTLED
import hristostefanov.minibankingdemo.business.entities.Transaction
import hristostefanov.minibankingdemo.business.interactors.CalcRoundUpInteractorImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.comparesEqualTo
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId

@ExperimentalCoroutinesApi
class CalcRoundUpInteractorImplTest {

    private val repository = mock(Repository::class.java)

    @Suppress("UNCHECKED_CAST")
    private val interactor by lazy {
        CalcRoundUpInteractorImpl(repository, someZone)
    }

    private val someDate = LocalDate.of(2020, Month.MARCH, 18)
    private val someZone = ZoneId.of("EET")
    private val someAccountId = "someId"


    @Test
    fun `Interacting with dependences`() = runBlockingTest {
        given(repository.findTransactions(any(), any())).willReturn(emptyList())

        interactor.execute(someAccountId, someDate)

        then(repository).should().findTransactions(someAccountId, someDate.atStartOfDay(someZone))
        then(repository).shouldHaveNoMoreInteractions()
    }

    @Test
    fun `Ignoring inbound transactions`() = runBlockingTest {
        given(repository.findTransactions(any(), any())).willReturn(
            listOf(
                // the amount of inbound transactions is positive!
                Transaction(
                    "1.3".toBigDecimal(),
                    SETTLED,
                    EXTERNAL
                )
            )
        )

        val result = interactor.execute(someAccountId, someDate)

        assertThat(result, comparesEqualTo(BigDecimal("0.00")))
    }

    @Test
    fun `Ignoring unsettled transactions`() = runBlockingTest {
        given(repository.findTransactions(any(), any())).willReturn(
            listOf(
                Transaction(
                    "-1.3".toBigDecimal(),
                    UNSETTLED,
                    EXTERNAL
                )
            )
        )

        val result = interactor.execute(someAccountId, someDate)

        assertThat(result, comparesEqualTo(BigDecimal("0.00")))
    }

    @Test
    fun `Ignoring internal transactions`() = runBlockingTest {
        given(repository.findTransactions(any(), any())).willReturn(
            listOf(
                Transaction(
                    "-1.3".toBigDecimal(),
                    SETTLED,
                    INTERNAL
                )
            )
        )

        val result = interactor.execute(someAccountId, someDate)

        assertThat(result, comparesEqualTo(BigDecimal("0.00")))
    }

    @Test
    fun `Ignoring zero amount transactions`() = runBlockingTest {
        given(repository.findTransactions(any(), any())).willReturn(
            listOf(
                Transaction(
                    "0.00".toBigDecimal(),
                    SETTLED,
                    EXTERNAL
                )
            )
        )

        val result = interactor.execute(someAccountId, someDate)

        assertThat(result, comparesEqualTo(BigDecimal("0.00")))
    }

    @Test
    fun `Max roundup`() = runBlockingTest {
        given(repository.findTransactions(any(), any())).willReturn(
            listOf(
                Transaction(
                    "-0.01".toBigDecimal(),
                    SETTLED,
                    EXTERNAL
                )
            )
        )

        val result = interactor.execute(someAccountId, someDate)

        assertThat(result, comparesEqualTo(BigDecimal("0.99")))
    }

    @Test
    fun `No roundup`() = runBlockingTest {
        given(repository.findTransactions(any(), any())).willReturn(
            listOf(
                Transaction(
                    "-4.00".toBigDecimal(),
                    SETTLED,
                    EXTERNAL
                ),
                Transaction(
                    "-5.00".toBigDecimal(),
                    SETTLED,
                    EXTERNAL
                )
            )
        )

        val result = interactor.execute(someAccountId, someDate)

        assertThat(result, comparesEqualTo(BigDecimal.ZERO))
    }

    @Test
    fun `No transactions`() = runBlockingTest {
        given(repository.findTransactions(any(), any())).willReturn(emptyList())

        val result = interactor.execute(someAccountId, someDate)

        assertThat(result, comparesEqualTo(BigDecimal.ZERO))
    }
}