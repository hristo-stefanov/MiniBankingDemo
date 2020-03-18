package hristostefanov.starlingdemo.business

import hristostefanov.starlingdemo.any
import hristostefanov.starlingdemo.business.dependences.Repository
import hristostefanov.starlingdemo.business.entities.Source.*
import hristostefanov.starlingdemo.business.entities.Status.SETTLED
import hristostefanov.starlingdemo.business.entities.Status.UNSETTLED
import hristostefanov.starlingdemo.business.entities.Transaction
import hristostefanov.starlingdemo.business.interactors.CalcRoundUpInteractor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.comparesEqualTo
import org.junit.Assert.assertThat
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId

@ExperimentalCoroutinesApi
class CalcRoundUpInteractorTest {

    private val repository = mock(Repository::class.java)
    private val interactor by lazy {
        CalcRoundUpInteractor(
            repository
        )
    }
    private val anyDate = LocalDate.now()
    private val anyZone = ZoneId.systemDefault()

    // TODO check internactions

    @Test
    fun `Specification example case`() = runBlockingTest {
        given(repository.findTransactions(any(), any())).willReturn(
            listOf(
                Transaction(
                    "-4.35".toBigDecimal(),
                    SETTLED,
                    EXTERNAL
                ),
                Transaction(
                    "-5.20".toBigDecimal(),
                    SETTLED,
                    EXTERNAL
                ),
                Transaction(
                    "-0.87".toBigDecimal(),
                    SETTLED,
                    EXTERNAL
                )
            )
        )

        val result = interactor.execute("anyId", anyDate, anyZone)

        assertThat(result, comparesEqualTo(BigDecimal("1.58")))
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

        val result = interactor.execute("anyId", anyDate, anyZone)

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

        val result = interactor.execute("anyId", anyDate, anyZone)

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

        val result = interactor.execute("anyId", anyDate, anyZone)

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

        val result = interactor.execute("anyId", anyDate, anyZone)

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

        val result = interactor.execute("anyId", anyDate, anyZone)

        assertThat(result, comparesEqualTo(BigDecimal("0.99")))
    }

    @Test()
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

        val result = interactor.execute("anyId", anyDate, anyZone)

        assertThat(result, comparesEqualTo(BigDecimal.ZERO))
    }

    @Test()
    fun `No transactions`() = runBlockingTest {
        given(repository.findTransactions(any(), any())).willReturn(emptyList())

        val result = interactor.execute("anyId", anyDate, anyZone)

        assertThat(result, comparesEqualTo(BigDecimal.ZERO))
    }
}