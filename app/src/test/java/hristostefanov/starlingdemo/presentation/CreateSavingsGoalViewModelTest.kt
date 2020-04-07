package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.SavedStateHandle
import hristostefanov.starlingdemo.any
import hristostefanov.starlingdemo.business.dependences.ServiceException
import hristostefanov.starlingdemo.business.interactors.CreateSavingsGoalInteractor
import hristostefanov.starlingdemo.ui.CreateSavingsGoalFragmentDirections
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.mockito.Mockito.timeout
import java.util.*

private const val TIMEOUT = 100L

class CreateSavingsGoalViewModelTest: BaseViewModelTest() {

    private val createSavingsGoalsIterator = mock(CreateSavingsGoalInteractor::class.java)
    private val initialStateMap = mutableMapOf<String, Any>()

    // test data
    private val gbp = Currency.getInstance("GBP")
    private val oneHundred = "100.00".toBigDecimal()
    private val account1Id = "1"
    private val goal1Name = "Goal1"
    private val error1 = "Error 1"

    private val savedStateHandle by lazy {
        SavedStateHandle(initialStateMap)
    }

    private val viewModelUnderTest by lazy {
        CreateSavingsGoalViewModel(savedStateHandle).apply {
            // manual field injection
            createSavingsGoalInteractor = createSavingsGoalsIterator
        }
    }

    @Test
    fun `Goal name changes are saved`() = runBlocking {
        givenValidInitialState()

        viewModelUnderTest.onNameChanged(goal1Name)

        assertThat(savedStateHandle[NAME_KEY], `is`(goal1Name))
    }

    @Test
    fun `Interaction and argument passing`() = runBlocking {
        givenValidInitialState()
        viewModelUnderTest.onNameChanged(goal1Name)
        given(createSavingsGoalsIterator.validateName(any())).willReturn(true)

        viewModelUnderTest.createCommand.execute()

        then(createSavingsGoalsIterator).should().validateName(goal1Name)
        then(createSavingsGoalsIterator).should(timeout(TIMEOUT)).execute(goal1Name, account1Id, gbp)
        then(createSavingsGoalsIterator).shouldHaveNoMoreInteractions()
    }

    @Test
    fun `Interactor succeeds - navigation`() = runBlocking {
        givenValidInitialState()
        initialStateMap[NAME_KEY] = goal1Name
        given(createSavingsGoalsIterator.validateName(any())).willReturn(true)
        given(createSavingsGoalsIterator.execute(any(), any(), any())).willReturn(Unit)

        viewModelUnderTest.createCommand.execute()

        val dir = viewModelUnderTest.navigationChannel.receive()
        assertThat(dir, equalTo(CreateSavingsGoalFragmentDirections.actionToSavingsGoalsDestination(account1Id, gbp, oneHundred)))
    }

    @Test
    fun `Interactor fails - navigation`() = runBlocking {
        givenValidInitialState()
        initialStateMap[NAME_KEY] = goal1Name
        given(createSavingsGoalsIterator.validateName(any())).willReturn(true)
        given(createSavingsGoalsIterator.execute(any(), any(), any())).willThrow(ServiceException(error1))

        viewModelUnderTest.createCommand.execute()

        val dir = viewModelUnderTest.navigationChannel.receive()
        assertThat(dir, equalTo(CreateSavingsGoalFragmentDirections.toErrorDialog(error1)))
    }


    private fun givenValidInitialState() {
        initialStateMap[ACCOUNT_ID_KEY] = account1Id
        initialStateMap[ACCOUNT_CURRENCY_KEY] = gbp
        initialStateMap[ROUND_UP_AMOUNT_KEY] = oneHundred
    }
}