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
import hristostefanov.starlingdemo.databinding.AccountsFragmentBinding
import hristostefanov.starlingdemo.presentation.AccountsViewModel
import kotlinx.android.synthetic.main.accounts_fragment.*

class AccountsFragment : Fragment() {
    private lateinit var binding: AccountsFragmentBinding
    private lateinit var viewModel: AccountsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AccountsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = (requireActivity().application as App).viewModelFactory
        viewModel = ViewModelProvider(this, viewModelFactory)[AccountsViewModel::class.java]

        // needed for observing LiveData
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        transferButton.setOnClickListener {
            viewModel.onTransferCommand()
        }

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

