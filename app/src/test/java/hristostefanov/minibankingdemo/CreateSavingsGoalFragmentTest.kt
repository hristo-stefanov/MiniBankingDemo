package hristostefanov.minibankingdemo

import android.os.Build
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import hristostefanov.minibankingdemo.business.interactors.CreateSavingsGoalInteractor
import hristostefanov.minibankingdemo.presentation.CreateSavingsGoalViewModel
import hristostefanov.minibankingdemo.ui.CreateSavingsGoalFragment
import hristostefanov.minibankingdemo.ui.CreateSavingsGoalFragmentArgs
import hristostefanov.minibankingdemo.util.LoginSessionRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.robolectric.annotation.Config
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O], application = HiltTestApplication::class)
class CreateSavingsGoalFragmentTest {
    @get:Rule(order = 0)
    internal val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    internal val mockitoRule = MockitoJUnit.rule()

    @get:Rule(order = 2)
    internal val coroutinesTestRule = CoroutinesTestRule()

    @BindValue // Hilt-testing: use this instance instead of creating an instance of the class
    @Mock
    internal lateinit var interactor: CreateSavingsGoalInteractor

    @Inject
    internal lateinit var loginSessionRegistry: LoginSessionRegistry

    private val argBundle = CreateSavingsGoalFragmentArgs("1", Currency.getInstance("GBP")).toBundle()

    private val navController =
        TestNavHostController(ApplicationProvider.getApplicationContext()).apply {
            setGraph(R.navigation.nav_graph)
            setCurrentDestination(R.id.createSavingsGoalDestination)
        }

    @Before
    fun beforeEach() {
        // used for field injection
        hiltRule.inject()

        // the user is logged in (fake session)
        loginSessionRegistry.createSession("token", "Bearer")
    }

    @Test
    fun `Should push fragment arguments and Name text`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            given(interactor.validateName("a")).willReturn(true)
            launchFragment(argBundle)
            onView(withId(R.id.nameEditText)).perform(ViewActions.typeText("a"))

            onView(withId(R.id.createSavingsGoalButton)).perform(click())

            then(interactor).should().execute("a", "1", Currency.getInstance("GBP"))
        }

    @Test
    fun `Should execute CreateSavingsGoalInteractor if enabled Create button is clicked`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            givenCreateButtonIsEnabled()

            onView(withId(R.id.createSavingsGoalButton)).perform(click())

            then(interactor).should().execute(any(), any(), any())
        }

    @Test
    fun `Should pull Name text if saved`() {
        argBundle.putString(CreateSavingsGoalViewModel.NAME_KEY, "saved")

        launchFragment(argBundle)

        onView(withId(R.id.nameEditText)).check(matches(withText("saved")))
    }

    @Test
    fun `Should pull Create button enabled state`() {
        given(interactor.validateName(any())).willReturn(true)

        launchFragment(argBundle)

        onView(withId(R.id.nameEditText)).perform(ViewActions.typeText("a"))
        onView(withId(R.id.createSavingsGoalButton)).check(matches(isEnabled()))
    }

    @Test
    fun `Should pull Create button disabled sate`() {
        given(interactor.validateName(any())).willReturn(false)

        launchFragment(argBundle)

        onView(withId(R.id.nameEditText)).perform(ViewActions.typeText("a"))
        onView(withId(R.id.createSavingsGoalButton)).check(matches(not(isEnabled())))
    }

    private fun givenCreateButtonIsEnabled() {
        given(interactor.validateName(any())).willReturn(true)
        launchFragment(argBundle)
        onView(withId(R.id.nameEditText)).perform(ViewActions.typeText("any"))
    }

    private fun launchFragment(bundle: Bundle) {
        // launch the fragment in isolation - in empty activity
        launchFragmentInHiltContainer<CreateSavingsGoalFragment>(bundle) {
            Navigation.setViewNavController(
                requireView(),
                navController
            )
        }
    }
}