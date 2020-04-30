package hristostefanov.starlingdemo.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import hristostefanov.starlingdemo.databinding.CreateSavingsGoalFragmentBinding
import hristostefanov.starlingdemo.presentation.CreateSavingsGoalViewModel

class CreateSavingsGoalFragment : Fragment() {
    private lateinit var _binding: CreateSavingsGoalFragmentBinding

    private val args by navArgs<CreateSavingsGoalFragmentArgs>()

    private val viewModel: CreateSavingsGoalViewModel by viewModels {
        viewModelFactory { savedStateHandle ->
            CreateSavingsGoalViewModel(args, savedStateHandle).also { sessionComponent().inject(it) }
        }
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
    }
}
