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
import hristostefanov.starlingdemo.databinding.AccountsFragmentBinding
import hristostefanov.starlingdemo.presentation.AccountsViewModel

class AccountsFragment : Fragment() {
    private lateinit var binding: AccountsFragmentBinding

    private val viewModel: AccountsViewModel by viewModels {
        UIUnitTestRegistry.viewModelFactory ?: ViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AccountsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // needed for observing LiveData
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

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
        AbstractSavedStateViewModelFactory(this, null) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return AccountsViewModel(handle).also { sessionComponent().inject(it) } as T
        }
    }
}

