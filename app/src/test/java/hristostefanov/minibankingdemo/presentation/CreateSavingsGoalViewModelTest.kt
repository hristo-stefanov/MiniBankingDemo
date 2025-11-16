package hristostefanov.minibankingdemo.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import hristostefanov.minibankingdemo.MainDispatcherRule
import hristostefanov.minibankingdemo.any
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.interactors.CreateSavingsGoalInteractor
import hristostefanov.minibankingdemo.presentation.CreateSavingsGoalViewModel.Companion.NAME_KEY
import hristostefanov.minibankingdemo.ui.CreateSavingsGoalFragmentDirections
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.LoginSessionComponent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.Rule
import org.mockito.BDDMockito.*
import java.util.*

private const val TIMEOUT = 100L

class CreateSavingsGoalViewModelTest() {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val createSavingsGoalsIterator = mock(CreateSavingsGoalInteractor::class.java)

    @Suppress("UNCHECKED_CAST")
    private val mainCommandChannel = spy(Channel::class.java) as Channel<MainCommand>

    @Suppress("UNCHECKED_CAST")
    val commandEnabledObserver = spy(Observer::class.java) as Observer<Boolean>

    private val sessionRegistry = mock(LoginSessionRegistry::class.java)
    private val sessionComponent = mock(LoginSessionComponent::class.java)

    // test data
    private val gbp = Currency.getInstance("GBP")
    private val account1Id = "1"
    private val error1 = "Error 1"

    // the values of goal names do not matter because we mock the validation function
    private val validGoalName = "Goal1"
    private val invalidGoalName = ""

    private val savedState = SavedStateHandle(
        mapOf("accountId" to account1Id, "accountCurrency" to gbp)
    )

    private val viewModelUnderTest by lazy {
        CreateSavingsGoalViewModel(savedState, sessionRegistry, mainCommandChannel)
    }

    @Before
    fun beforeEach() = runTest {
        given(sessionRegistry.component).willReturn(sessionComponent)
        given(sessionComponent.createSavingGoalsInteractor).willReturn(createSavingsGoalsIterator)
    }

    @Test
    fun `WHEN name changes THEN name is saved`() = runTest {
        viewModelUnderTest.name.value = validGoalName

        assertThat(savedState[NAME_KEY], `is`(validGoalName))
    }

    @Test
    fun `GIVEN saved name is valid WHEN constructed THEN Create command is enabled`() {
        savedState[NAME_KEY] = validGoalName
        given(createSavingsGoalsIterator.validateName(any())).willReturn(true)

        viewModelUnderTest.createCommandEnabled.observeForever(commandEnabledObserver)

        // Note: validateName() is called twice - once LiveData.map() is invoked and once
        // when the observer is attached
        then(createSavingsGoalsIterator).should(times(2)).validateName(validGoalName)
        then(commandEnabledObserver).should().onChanged(true)
    }

    @Test
    fun `GIVEN saved name is invalid WHEN constructed THEN Create command is disabled`() {
        savedState[NAME_KEY] = invalidGoalName
        given(createSavingsGoalsIterator.validateName(any())).willReturn(false)

        viewModelUnderTest.createCommandEnabled.observeForever(commandEnabledObserver)


        // Note: validateName() is called twice - once LiveData.map() is invoked and once
        // when the observer is attached
        then(createSavingsGoalsIterator).should(times(2)).validateName(invalidGoalName)
        then(commandEnabledObserver).should().onChanged(false)
    }

    @Test
    fun `GIVEN invalid name WHEN name is changed to valid one THEN Create command is enabled`() {
        savedState[NAME_KEY] = invalidGoalName
        // Note: validateName() is called twice initially hence we need false twice
        given(createSavingsGoalsIterator.validateName(any())).willReturn(false).willReturn(false).willReturn(true)
        viewModelUnderTest.createCommandEnabled.observeForever(commandEnabledObserver)

        viewModelUnderTest.name.value = validGoalName

        // Note: validateName() is called twice - once LiveData.map() is invoked and once
        // when the observer is attached
        then(createSavingsGoalsIterator).should(times(2)).validateName(invalidGoalName)
        then(commandEnabledObserver).should().onChanged(false)
        then(commandEnabledObserver).should().onChanged(true)
        then(commandEnabledObserver).shouldHaveNoMoreInteractions()
    }

    @Test
    fun `GIVEN valid name WHEN name is changed to invalid one THEN Create command is disabled`() {
        savedState[NAME_KEY] = validGoalName
        // Note: validateName() is called twice initially hence we need true twice
        given(createSavingsGoalsIterator.validateName(any())).willReturn(true).willReturn(true).willReturn(false)
        viewModelUnderTest.createCommandEnabled.observeForever(commandEnabledObserver)

        viewModelUnderTest.name.value = invalidGoalName

        // Note: validateName() is called twice - once LiveData.map() is invoked and once
        // when the observer is attached
        then(createSavingsGoalsIterator).should(times(2)).validateName(validGoalName)
        then(commandEnabledObserver).should().onChanged(true)
        then(commandEnabledObserver).should().onChanged(false)
        then(commandEnabledObserver).shouldHaveNoMoreInteractions()
    }


    @Test
    fun `GIVEN name is valid WHEN executing Create command THEN will interact`() = runTest {
        viewModelUnderTest.name.value = validGoalName
        given(createSavingsGoalsIterator.validateName(any())).willReturn(true)

        viewModelUnderTest.onCreateCommand()

        then(createSavingsGoalsIterator).should().validateName(validGoalName)
        then(createSavingsGoalsIterator).should(timeout(TIMEOUT)).execute(validGoalName, account1Id, gbp)
        then(createSavingsGoalsIterator).shouldHaveNoMoreInteractions()
    }

    @Test
    fun `GIVEN name is invalid WHEN executing Create command THEN will not interact`() {
        viewModelUnderTest.name.value = invalidGoalName
        given(createSavingsGoalsIterator.validateName(any())).willReturn(false)

        viewModelUnderTest.onCreateCommand()

        then(createSavingsGoalsIterator).should().validateName(invalidGoalName)
        then(createSavingsGoalsIterator).shouldHaveNoMoreInteractions()
    }

    @Test
    fun `WHEN interactor succeeds THEN navigate back`() = runTest {
        savedState[NAME_KEY] = validGoalName
        given(createSavingsGoalsIterator.validateName(any())).willReturn(true)
        given(createSavingsGoalsIterator.execute(any(), any(), any())).willReturn(Unit)

        viewModelUnderTest.onCreateCommand()

        then(mainCommandChannel).should(timeout(TIMEOUT)).send(MainCommand.NavigateBackward)
    }

    @Test
    fun `WHEN interactor fails THEN navigate to error dialog`() = runTest {
        savedState[NAME_KEY] = validGoalName
        given(createSavingsGoalsIterator.validateName(any())).willReturn(true)
        given(createSavingsGoalsIterator.execute(any(), any(), any())).willThrow(ServiceException(error1))

        viewModelUnderTest.onCreateCommand()

        then(mainCommandChannel).should(timeout(TIMEOUT)).send(MainCommand.NavigateForward(CreateSavingsGoalFragmentDirections.toErrorDialog(error1)))
    }
}