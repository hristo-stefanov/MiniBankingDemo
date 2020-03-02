package hristostefanov.starlingdemo.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import hristostefanov.starlingdemo.App
import hristostefanov.starlingdemo.R
import hristostefanov.starlingdemo.presentation.TransferConfirmationViewModel
import kotlinx.android.synthetic.main.transfer_confirmation_fragment.*

class TransferConfirmationFragment : Fragment() {

    private lateinit var viewModel: TransferConfirmationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.transfer_confirmation_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = (requireActivity().application as App).viewModelFactory
        viewModel =
            ViewModelProvider(this, viewModelFactory)[TransferConfirmationViewModel::class.java]

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

        // launch a lifecycle aware coroutine
        lifecycleScope.launchWhenStarted {
            // the terminating condition of the loop is the cancellation of the coroutine
            while (true) {
                val directions = viewModel.navigationChannel.receive()
                findNavController().navigate(directions)
            }
        }

        confirmButton.setOnClickListener {
            viewModel.onConfirmCommand()
        }
    }
}
