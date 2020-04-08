package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.SavedStateHandle
import hristostefanov.starlingdemo.any
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.*
import org.junit.Test
import org.mockito.BDDMockito.*
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.util.function.Consumer
import java.util.function.Predicate

@Suppress("UNCHECKED_CAST")
class CommandImplTest : BaseViewModelTest() {

    private val state = SavedStateHandle()
    private lateinit var keys: List<String>

    private val consumer = mock(Consumer::class.java) as Consumer<SavedStateHandle>
    private val predicate = mock(Predicate::class.java) as Predicate<SavedStateHandle>

    val cmdUnderTest by lazy {
        CommandImpl(state, predicate, keys, consumer)
    }

    @Test
    fun `Initial state predicate returning false`() {
        given(predicate.test(any())).willReturn(false)

        keys = listOf("KEY1")

        // null values should be explicitly set
        state["KEY1"] = null

        var result: Boolean? = null
        cmdUnderTest.enabledLive.observeForever {
            result = it
        }

        assertThat(result, equalTo(false))
        then(predicate).should(Mockito.times(1)).test(state)
    }


    @Test
    fun `Initial state predicate returning true`() {
        given(predicate.test(any())).willReturn(true)

        keys = listOf("KEY1")

        // null values should be explicitly set
        state["KEY1"] = null

        var result: Boolean? = null
        cmdUnderTest.enabledLive.observeForever {
            result = it
        }

        assertThat(result, equalTo(true))
        then(predicate).should(Mockito.times(1)).test(state)
    }

    @Test
    fun `Test predicate on state change`() {
        given(predicate.test(any())).willReturn(true).willReturn(false)

        keys = listOf("KEY1")
        // null values should be explicitly set
        state["KEY1"] = null

        var result: Boolean? = null
        cmdUnderTest.enabledLive.observeForever {
            result = it
        }

        assertThat(result, `is`(true))

        state["KEY1"] = "Zen"

        assertThat(result, `is`(false))
        then(predicate).should(Mockito.times(2)).test(state)
    }

    @Test
    fun `Observing inital state with two keys`() {
        keys = listOf("KEY1", "KEY2")
        state["KEY1"] = null
        state["KEY2"] = null

        cmdUnderTest.enabledLive.observeForever {
        }

        then(predicate).should(Mockito.times(2)).test(state)
    }

    @Test
    fun `Observing change of one of two`() {
        keys = listOf("KEY1", "KEY2")
        state["KEY1"] = null
        state["KEY2"] = null

        var result: Boolean? = null
        cmdUnderTest.enabledLive.observeForever {
            result = it
        }

        state["KEY2"] = "change"

        then(predicate).should(Mockito.times(3)).test(state)
    }

    @Test
    fun `Execution blocked`() {
        keys = listOf("KEY1")
        given(predicate.test(any())).willReturn(false)

        cmdUnderTest.execute()

        then(consumer).shouldHaveNoInteractions()
    }

    @Test
    fun `Execution accepted`() {
        keys = listOf("KEY1")
        given(predicate.test(any())).willReturn(true)

        cmdUnderTest.execute()

        then(consumer).should().accept(state)
    }

}
