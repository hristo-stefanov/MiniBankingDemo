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
    private lateinit var binding: CreateSavingsGoalFragmentBinding

    private val args by navArgs<CreateSavingsGoalFragmentArgs>()

    private val viewModel: CreateSavingsGoalViewModel by viewModels {
        viewModelFactory { savedStateHandle ->
            sessionComponent().getCreateSavingsGoalViewModel().apply { init(args, savedStateHandle) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CreateSavingsGoalFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this // needed for observing LiveData
    }
}
