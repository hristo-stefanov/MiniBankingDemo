package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import hristostefanov.starlingdemo.any
import hristostefanov.starlingdemo.business.dependences.ServiceException
import hristostefanov.starlingdemo.business.interactors.CreateSavingsGoalInteractor
import hristostefanov.starlingdemo.presentation.CreateSavingsGoalViewModel.Companion.NAME_KEY
import hristostefanov.starlingdemo.ui.CreateSavingsGoalFragmentArgs
import hristostefanov.starlingdemo.ui.CreateSavingsGoalFragmentDirections
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import org.mockito.BDDMockito.*
import java.util.*

private const val TIMEOUT = 100L

class CreateSavingsGoalViewModelTest: BaseViewModelTest() {

    private val createSavingsGoalsIterator = mock(CreateSavingsGoalInteractor::class.java)

    @Suppress("UNCHECKED_CAST")
    private val navigationChannel = spy(Channel::class.java) as Channel<Navigation>

    @Suppress("UNCHECKED_CAST")
    val commandEnabledObserver = spy(Observer::class.java) as Observer<Boolean>

    // test data
    private val gbp = Currency.getInstance("GBP")
    private val account1Id = "1"
    private val error1 = "Error 1"

    // the values of goal names do not matter because we mock the validation function
    private val validGoalName = "Goal1"
    private val invalidGoalName = ""

    private val savedState = SavedStateHandle()
    private val validArgs = CreateSavingsGoalFragmentArgs(account1Id, gbp)

    private val viewModelUnderTest by lazy {
        CreateSavingsGoalViewModel(validArgs, savedState).also {
            // manual field injection
            it.createSavingsGoalInteractor = createSavingsGoalsIterator
            it.navigationChannel = navigationChannel
        }
    }

    @Test
    fun `WHEN name changes THEN name is saved`() = runBlocking {
        viewModelUnderTest.onNameChanged(validGoalName)

        assertThat(savedState[NAME_KEY], `is`(validGoalName))
    }

    @Test
    fun `GIVEN saved name is valid WHEN constructed THEN Create command is enabled`() {
        savedState[NAME_KEY] = validGoalName
        given(createSavingsGoalsIterator.validateName(any())).willReturn(true)

        viewModelUnderTest.createCommandEnabled.observeForever(commandEnabledObserver)

        then(createSavingsGoalsIterator).should().validateName(validGoalName)
        then(commandEnabledObserver).should().onChanged(true)
    }

    @Test
    fun `GIVEN saved name is invalid WHEN constructed THEN Create command is disabled`() {
        savedState[NAME_KEY] = invalidGoalName
        given(createSavingsGoalsIterator.validateName(any())).willReturn(false)

        viewModelUnderTest.createCommandEnabled.observeForever(commandEnabledObserver)

        then(createSavingsGoalsIterator).should().validateName(invalidGoalName)
        then(commandEnabledObserver).should().onChanged(false)
    }

    @Test
    fun `GIVEN invalid name WHEN name is changed to valid one THEN Create command is enabled`() {
        savedState[NAME_KEY] = invalidGoalName
        given(createSavingsGoalsIterator.validateName(any())).willReturn(false).willReturn(true)
        viewModelUnderTest.createCommandEnabled.observeForever(commandEnabledObserver)

        viewModelUnderTest.onNameChanged(validGoalName)

        then(createSavingsGoalsIterator).should().validateName(invalidGoalName)
        then(commandEnabledObserver).should().onChanged(false)
        then(commandEnabledObserver).should().onChanged(true)
        then(commandEnabledObserver).shouldHaveNoMoreInteractions()
    }

    @Test
    fun `GIVEN valid name WHEN name is changed to invalid one THEN Create command is disabled`() {
        savedState[NAME_KEY] = validGoalName
        given(createSavingsGoalsIterator.validateName(any())).willReturn(true).willReturn(false)
        viewModelUnderTest.createCommandEnabled.observeForever(commandEnabledObserver)

        viewModelUnderTest.onNameChanged(invalidGoalName)

        then(createSavingsGoalsIterator).should().validateName(validGoalName)
        then(commandEnabledObserver).should().onChanged(true)
        then(commandEnabledObserver).should().onChanged(false)
        then(commandEnabledObserver).shouldHaveNoMoreInteractions()
    }


    @Test
    fun `GIVEN name is valid WHEN executing Create command THEN will interact`() = runBlocking {
        viewModelUnderTest.onNameChanged(validGoalName)
        given(createSavingsGoalsIterator.validateName(any())).willReturn(true)

        viewModelUnderTest.onCreateCommand()

        then(createSavingsGoalsIterator).should().validateName(validGoalName)
        then(createSavingsGoalsIterator).should(timeout(TIMEOUT)).execute(validGoalName, account1Id, gbp)
        then(createSavingsGoalsIterator).shouldHaveNoMoreInteractions()
    }

    @Test
    fun `GIVEN name is invalid WHEN executing Create command THEN will not interact`() {
        viewModelUnderTest.onNameChanged(invalidGoalName)
        given(createSavingsGoalsIterator.validateName(any())).willReturn(false)

        viewModelUnderTest.onCreateCommand()

        then(createSavingsGoalsIterator).should().validateName(invalidGoalName)
        then(createSavingsGoalsIterator).shouldHaveNoMoreInteractions()
    }

    @Test
    fun `WHEN interactor succeeds THEN navigate back`() = runBlocking {
        savedState[NAME_KEY] = validGoalName
        given(createSavingsGoalsIterator.validateName(any())).willReturn(true)
        given(createSavingsGoalsIterator.execute(any(), any(), any())).willReturn(Unit)

        viewModelUnderTest.onCreateCommand()

        then(navigationChannel).should(timeout(TIMEOUT)).send(Navigation.Backward)
    }

    @Test
    fun `WHEN interactor fails THEN navigate to error dialog`() = runBlocking {
        savedState[NAME_KEY] = validGoalName
        given(createSavingsGoalsIterator.validateName(any())).willReturn(true)
        given(createSavingsGoalsIterator.execute(any(), any(), any())).willThrow(ServiceException(error1))

        viewModelUnderTest.onCreateCommand()

        then(navigationChannel).should(timeout(TIMEOUT)).send(Navigation.Forward(CreateSavingsGoalFragmentDirections.toErrorDialog(error1)))
    }
}