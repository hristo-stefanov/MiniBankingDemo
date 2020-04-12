package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.SavedStateHandle
import hristostefanov.starlingdemo.any
import hristostefanov.starlingdemo.business.dependences.ServiceException
import hristostefanov.starlingdemo.business.interactors.CreateSavingsGoalInteractor
import hristostefanov.starlingdemo.presentation.CreateSavingsGoalViewModel.Companion.name
import hristostefanov.starlingdemo.ui.CreateSavingsGoalFragmentArgs
import hristostefanov.starlingdemo.ui.CreateSavingsGoalFragmentDirections
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
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
    // TODO mocking a type that do not own
    private val eventBus = mock(EventBus::class.java)

    // test data
    private val gbp = Currency.getInstance("GBP")
    private val account1Id = "1"
    private val goal1Name = "Goal1"
    private val error1 = "Error 1"

    private val state = SavedStateHandle()
    private val validArgs = CreateSavingsGoalFragmentArgs(account1Id, gbp)

    private val viewModelUnderTest by lazy {
        CreateSavingsGoalViewModel(validArgs, state).also {
            // manual field injection
            it.createSavingsGoalInteractor = createSavingsGoalsIterator
            it.eventBus = eventBus
        }
    }

    @Test
    fun `Goal name changes are saved`() = runBlocking {
        viewModelUnderTest.onNameChanged(goal1Name)

        assertThat(state.name, `is`(goal1Name))
    }

    @Test
    fun `Interaction and argument passing`() = runBlocking {
        viewModelUnderTest.onNameChanged(goal1Name)
        given(createSavingsGoalsIterator.validateName(any())).willReturn(true)

        viewModelUnderTest.createCommand.execute()

        then(createSavingsGoalsIterator).should().validateName(goal1Name)
        then(createSavingsGoalsIterator).should(timeout(TIMEOUT)).execute(goal1Name, account1Id, gbp)
        then(createSavingsGoalsIterator).shouldHaveNoMoreInteractions()
    }

    @Test
    fun `Interactor succeeds - navigation`() = runBlocking {
        state.name = goal1Name
        given(createSavingsGoalsIterator.validateName(any())).willReturn(true)
        given(createSavingsGoalsIterator.execute(any(), any(), any())).willReturn(Unit)

        viewModelUnderTest.createCommand.execute()

        then(eventBus).should(timeout(TIMEOUT)).post(Navigation.Backward)
    }

    @Test
    fun `Interactor fails - navigation`() = runBlocking {
        state.name = goal1Name
        given(createSavingsGoalsIterator.validateName(any())).willReturn(true)
        given(createSavingsGoalsIterator.execute(any(), any(), any())).willThrow(ServiceException(error1))

        viewModelUnderTest.createCommand.execute()

        then(eventBus).should(timeout(TIMEOUT)).post(Navigation.Forward(CreateSavingsGoalFragmentDirections.toErrorDialog(error1)))
    }
}