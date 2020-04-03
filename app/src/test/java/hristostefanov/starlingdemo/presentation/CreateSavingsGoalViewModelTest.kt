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
import java.util.*

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

    // Rule: disabled commands should not run
    @Test
    fun `Init with no name - onCreate`() = runBlocking {
        givenValidInitialState()
        given(createSavingsGoalsIterator.execute(any(), any(), any())).willReturn(Unit)

        viewModelUnderTest.onCreateCommand()

        then(createSavingsGoalsIterator).shouldHaveNoInteractions()
    }

    @Test
    fun `Init with no name - observe createCommandEnabled`() {
        givenValidInitialState()

        var result: Boolean? = null
        viewModelUnderTest.createCommandEnabled.observeForever {
            result = it
        }

        assertThat(result, `is`(false))
    }

    @Test
    fun `Init with invalid name - observe createCommandEnabled`() {
        givenValidInitialState()
        initialStateMap[NAME_KEY] = ""

        var result: Boolean? = null
        viewModelUnderTest.createCommandEnabled.observeForever {
            result = it
        }

        assertThat(result, `is`(false))
    }

    @Test
    fun `Init with valid name - observe createCommandEnabled`() {
        givenValidInitialState()
        initialStateMap[NAME_KEY] = goal1Name

        var result: Boolean? = null
        viewModelUnderTest.createCommandEnabled.observeForever {
            result = it
        }

        assertThat(result, `is`(true))
    }

    @Test
    fun `Goal name changes are saved`() = runBlocking {
        givenValidInitialState()

        viewModelUnderTest.onNameChanged(goal1Name)

        assertThat(savedStateHandle[NAME_KEY], `is`(goal1Name))
    }

    @Test
    fun `Should Interact on Create command`() = runBlocking {
        givenValidInitialState()
        viewModelUnderTest.onNameChanged(goal1Name)

        viewModelUnderTest.onCreateCommand()

        then(createSavingsGoalsIterator).should().execute(goal1Name, account1Id, gbp)
    }

    @Test
    fun `Create succeeds`() = runBlocking {
        givenValidInitialState()
        given(createSavingsGoalsIterator.execute(any(), any(), any())).willReturn(Unit)

        viewModelUnderTest.onCreateCommand()

        val dir = viewModelUnderTest.navigationChannel.receive()
        assertThat(dir, equalTo(CreateSavingsGoalFragmentDirections.actionToSavingsGoalsDestination(account1Id, gbp, oneHundred)))
    }

    @Test
    fun `Create fails`() = runBlocking {
        givenValidInitialState()
        given(createSavingsGoalsIterator.execute(any(), any(), any())).willThrow(ServiceException(error1))

        viewModelUnderTest.onCreateCommand()

        val dir = viewModelUnderTest.navigationChannel.receive()
        assertThat(dir, equalTo(CreateSavingsGoalFragmentDirections.toErrorDialog(error1)))
    }


    private fun givenValidInitialState() {
        initialStateMap[ACCOUNT_ID_KEY] = account1Id
        initialStateMap[ACCOUNT_CURRENCY_KEY] = gbp
        initialStateMap[ROUND_UP_AMOUNT_KEY] = oneHundred
    }
}