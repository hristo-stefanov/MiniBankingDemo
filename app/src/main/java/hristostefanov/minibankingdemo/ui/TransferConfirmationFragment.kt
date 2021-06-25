package hristostefanov.minibankingdemo.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import hristostefanov.minibankingdemo.databinding.TransferConfirmationFragmentBinding
import hristostefanov.minibankingdemo.presentation.TransferConfirmationViewModel

class TransferConfirmationFragment : Fragment() {

    private val viewModel: TransferConfirmationViewModel by viewModels {
        viewModelFactory {
            TransferConfirmationViewModel(args).also { sessionComponent().inject(it) }
        }
    }

    private val args: TransferConfirmationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = TransferConfirmationFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // launch a lifecycle aware coroutine
        lifecycleScope.launchWhenStarted {
            // the terminating condition of the loop is the cancellation of the coroutine
            while (true) {
                val acknowledgement = viewModel.acknowledgementChannel.receive()
                Snackbar.make(view, acknowledgement, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
