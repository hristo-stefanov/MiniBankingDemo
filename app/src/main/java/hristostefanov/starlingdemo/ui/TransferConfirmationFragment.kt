package hristostefanov.starlingdemo.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import hristostefanov.starlingdemo.R
import hristostefanov.starlingdemo.presentation.TransferConfirmationViewModel
import kotlinx.android.synthetic.main.transfer_confirmation_fragment.*

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
    ): View? {
        return inflater.inflate(R.layout.transfer_confirmation_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.info.observe(viewLifecycleOwner, Observer {
            infoTextView.text = it
        })

        // launch a lifecycle aware coroutine
        lifecycleScope.launchWhenStarted {
            // the terminating condition of the loop is the cancellation of the coroutine
            while (true) {
                val acknowledgement = viewModel.acknowledgementChannel.receive()
                Snackbar.make(view, acknowledgement, Snackbar.LENGTH_LONG).show()
            }
        }

        confirmButton.setOnClickListener {
            viewModel.onConfirmCommand()
        }
    }
}
