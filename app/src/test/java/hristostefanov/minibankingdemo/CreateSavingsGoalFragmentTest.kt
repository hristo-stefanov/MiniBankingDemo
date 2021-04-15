package hristostefanov.minibankingdemo

import android.os.Build
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import hristostefanov.minibankingdemo.presentation.CreateSavingsGoalViewModel
import hristostefanov.minibankingdemo.ui.CreateSavingsGoalFragment
import hristostefanov.minibankingdemo.ui.UIUnitTestRegistry
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O])
class CreateSavingsGoalFragmentTest {

    private val viewModel = mock(CreateSavingsGoalViewModel::class.java)

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext()).apply {
        setGraph(R.navigation.nav_graph)
        setCurrentDestination(R.id.createSavingsGoalDestination)
    }

    @Before
    fun beforeEach() {
        // make the fragment use the mock view model
        UIUnitTestRegistry.viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return viewModel as T
            }
        }
    }

    @Test()
    fun buttonEnabled() {
        given(viewModel.createCommandEnabled).willReturn(MutableLiveData(true))

        launchFragment()

        then(viewModel).should().createCommandEnabled
        onView(withId(R.id.createSavingsGoalButton)).check(matches(isEnabled()))
    }

    @Test
    fun buttonDisabled() {
        given(viewModel.createCommandEnabled).willReturn(MutableLiveData(false))

        launchFragment()

        then(viewModel).should().createCommandEnabled
        onView(withId(R.id.createSavingsGoalButton)).check(matches(not(isEnabled())))
    }

    @Test
    fun nameTextPassed() {
        given(viewModel.createCommandEnabled).willReturn(MutableLiveData(false))
        launchFragment()

        onView(withId(R.id.nameEditText)).perform(ViewActions.typeText("a"))

        then(viewModel).should().onNameChanged("a")
    }

    @Test
    fun buttonClicked() {
        given(viewModel.createCommandEnabled).willReturn(MutableLiveData(true))
        launchFragment()

        onView(withId(R.id.createSavingsGoalButton)).perform(click())

        then(viewModel).should().onCreateCommand()
    }

    private fun launchFragment() {
        // launch the fragment in isolation - in empty activity
        val fragmentScenario = launchFragmentInContainer<CreateSavingsGoalFragment>()
        fragmentScenario.onFragment(object : FragmentScenario.FragmentAction<CreateSavingsGoalFragment> {
            override fun perform(fragment: CreateSavingsGoalFragment) {
                Navigation.setViewNavController(fragment.requireView(), navController)
            }
        })
    }
}