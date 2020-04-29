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
import hristostefanov.starlingdemo.databinding.AccessTokenFragmentBinding
import hristostefanov.starlingdemo.presentation.AccessTokenViewModel
import kotlinx.android.synthetic.main.access_token_fragment.*

class AccessTokenFragment : Fragment() {
    private lateinit var binding: AccessTokenFragmentBinding

    private val viewModel: AccessTokenViewModel by viewModels {
        UIUnitTestRegistry.viewModelFactory ?: ViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AccessTokenFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this // needed for observing LiveData
        binding.viewmodel = viewModel

        accessTokenEditText.requestFocus()
    }

    private inner class ViewModelFactory :
        AbstractSavedStateViewModelFactory(this, null) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return AccessTokenViewModel(handle).also { sessionComponent().inject(it) } as T
        }
    }
}
