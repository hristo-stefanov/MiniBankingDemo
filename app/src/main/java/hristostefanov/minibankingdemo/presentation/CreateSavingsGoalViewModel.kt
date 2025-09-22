package hristostefanov.minibankingdemo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hristostefanov.minibankingdemo.ui.CreateSavingsGoalFragmentArgs
import hristostefanov.minibankingdemo.usecase.CancelCreateSavingsGoal
import hristostefanov.minibankingdemo.usecase.Continuation
import hristostefanov.minibankingdemo.usecase.ContinuationId
import hristostefanov.minibankingdemo.usecase.Trigger
import hristostefanov.minibankingdemo.util.ContinuationChannel
import hristostefanov.minibankingdemo.util.TriggerChannel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class CreateSavingsGoalViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    @ContinuationChannel
    private val continuationChannel: Channel<Continuation>,
    @TriggerChannel
    private val triggerChannel: Channel<Trigger>
) : ViewModel() {

    private val args = CreateSavingsGoalFragmentArgs.fromSavedStateHandle(savedState)
    // Another approach could be using @EntryPoint, see
    // https://medium.com/androiddevelopers/hilt-adding-components-to-the-hierarchy-96f207d6d92d

    companion object {
        const val NAME_KEY = "name"
    }

    // exposing MutableLiveData to allow two-way data binding
    val name: MutableLiveData<String> = savedState.getLiveData(NAME_KEY)

    // TODO validation rule
    private fun validateName(name: String) = name.isNotBlank()

    open val createCommandEnabled: LiveData<Boolean> by lazy {
        savedState.getLiveData<String>(NAME_KEY).map { name ->
            validateName(name) ?: false
            true
        }
    }

    fun onCancel() {
        viewModelScope.launch(start = CoroutineStart.ATOMIC) {
            triggerChannel.send(CancelCreateSavingsGoal)
        }
    }

    open fun onCreateCommand() {
        savedState.get<String>(NAME_KEY)?.also { name ->
            viewModelScope.launch {
                continuationChannel.send(Continuation(ContinuationId.valueOf(args.continuationId), listOf(name)))
            }
        }
    }
}