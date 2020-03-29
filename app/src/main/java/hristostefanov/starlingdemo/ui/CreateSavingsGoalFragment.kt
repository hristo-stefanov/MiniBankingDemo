package hristostefanov.starlingdemo.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import hristostefanov.starlingdemo.databinding.CreateSavingsGoalFragmentBinding
import hristostefanov.starlingdemo.presentation.CreateSavingsGoalViewModel

class CreateSavingsGoalFragment : Fragment() {
    private lateinit var _binding: CreateSavingsGoalFragmentBinding

    private val args by navArgs<CreateSavingsGoalFragmentArgs>()

    private val viewModel: CreateSavingsGoalViewModel by viewModels {
        UIUnitTestRegistry.viewModelFactory ?: ViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CreateSavingsGoalFragmentBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.viewmodel = viewModel
        _binding.lifecycleOwner = this // needed for observing LiveData

        // launch a lifecycle aware coroutine
        lifecycleScope.launchWhenStarted {
            // the terminating condition of the loop is the cancellation of the coroutine
            while (true) {
                val directions = viewModel.navigationChannel.receive()
                findNavController().navigate(directions)
            }
        }
    }

    private inner class ViewModelFactory :
        AbstractSavedStateViewModelFactory(this, args.toBundle()) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return CreateSavingsGoalViewModel(handle).also { sessionComponent().inject(it) } as T
        }
    }
}
