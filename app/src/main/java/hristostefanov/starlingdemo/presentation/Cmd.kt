package hristostefanov.starlingdemo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import java.util.function.Consumer
import java.util.function.Predicate

class Cmd(private val state: SavedStateHandle,
          private val predicate: Predicate<SavedStateHandle>,
          private val keys: List<String>,
          private val block: Consumer<SavedStateHandle>
): ICmd {

    override val enabledLive: LiveData<Boolean>

    private val mediator = MediatorLiveData<String>()

    init {
        keys.forEach { key ->
            val liveValue = state.getLiveData<Any>(key)
            mediator.addSource(liveValue) {
                mediator.value = key
            }
        }

        enabledLive = Transformations.map(mediator) {changeKey ->
            predicate.test(state)
        }
    }

    override fun execute() {
        if (predicate.test(state))
            block.accept(state)
    }
}

interface ICmd {
    val enabledLive: LiveData<Boolean>
    fun execute()
}
