package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import hristostefanov.minibankingdemo.any
import hristostefanov.minibankingdemo.business.dependences.ServiceException
import hristostefanov.minibankingdemo.business.interactors.CreateSavingsGoalInteractor
import hristostefanov.minibankingdemo.presentation.CreateSavingsGoalViewModel.Companion.NAME_KEY
import hristostefanov.minibankingdemo.ui.CreateSavingsGoalFragmentDirections
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import hristostefanov.minibankingdemo.util.LoginSessionComponent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
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
        CreateSavingsGoalViewModel(savedState, sessionRegistry, navigationChannel)
    }

    @Before
    fun beforeEach() {
        given(sessionRegistry.component).willReturn(sessionComponent)
        given(sessionComponent.createSavingGoalsInteractor).willReturn(createSavingsGoalsIterator)
    }

    @Test
    fun `WHEN name changes THEN name is saved`() = runBlocking {
        viewModelUnderTest.name.value = validGoalName

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

        viewModelUnderTest.name.value = validGoalName

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

        viewModelUnderTest.name.value = invalidGoalName

        then(createSavingsGoalsIterator).should().validateName(validGoalName)
        then(commandEnabledObserver).should().onChanged(true)
        then(commandEnabledObserver).should().onChanged(false)
        then(commandEnabledObserver).shouldHaveNoMoreInteractions()
    }


    @Test
    fun `GIVEN name is valid WHEN executing Create command THEN will interact`() = runBlocking {
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