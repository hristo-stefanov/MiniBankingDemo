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
import hristostefanov.starlingdemo.databinding.AccessTokenFragmentBinding
import hristostefanov.starlingdemo.presentation.AccessTokenViewModel

class AccessTokenFragment : Fragment() {
    private lateinit var binding: AccessTokenFragmentBinding
    private lateinit var viewModel: AccessTokenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AccessTokenFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = (requireActivity().application as App).viewModelFactory
        viewModel = ViewModelProvider(this, viewModelFactory)[AccessTokenViewModel::class.java]

        binding.lifecycleOwner = this // needed for observing LiveData
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
}
