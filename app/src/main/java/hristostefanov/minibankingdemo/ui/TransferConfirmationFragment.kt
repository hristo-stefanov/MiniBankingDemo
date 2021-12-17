package hristostefanov.minibankingdemo.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hristostefanov.minibankingdemo.databinding.TransferConfirmationFragmentBinding
import hristostefanov.minibankingdemo.presentation.TransferConfirmationViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class TransferConfirmationFragment : Fragment() {

    private val viewModel: TransferConfirmationViewModel by viewModels()

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

        viewModel.acknowledgement
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}
