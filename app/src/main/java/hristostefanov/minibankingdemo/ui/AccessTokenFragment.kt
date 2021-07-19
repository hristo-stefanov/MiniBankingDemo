package hristostefanov.minibankingdemo.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import hristostefanov.minibankingdemo.databinding.AccessTokenFragmentBinding
import hristostefanov.minibankingdemo.presentation.AccessTokenViewModel

class AccessTokenFragment : Fragment() {
    private lateinit var binding: AccessTokenFragmentBinding

    private val viewModel: AccessTokenViewModel by viewModels {
        viewModelFactory { savedStateHandle ->
            AccessTokenViewModel(savedStateHandle).also { sessionComponent().inject(it) }
        }
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

        binding.accessTokenEditText.requestFocus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
    }
}
