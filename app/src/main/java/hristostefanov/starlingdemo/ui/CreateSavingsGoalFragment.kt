package hristostefanov.starlingdemo.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import hristostefanov.starlingdemo.App
import hristostefanov.starlingdemo.databinding.CreateSavingsGoalFragmentBinding
import hristostefanov.starlingdemo.presentation.CreateSavingsGoalViewModel

class CreateSavingsGoalFragment : Fragment() {
    private lateinit var _binding: CreateSavingsGoalFragmentBinding
    private lateinit var viewModel: CreateSavingsGoalViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CreateSavingsGoalFragmentBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = (requireActivity().application as App).viewModelFactory
        viewModel =
            ViewModelProvider(this, viewModelFactory)[CreateSavingsGoalViewModel::class.java]

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
}
