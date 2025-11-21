package hristostefanov.minibankingdemo.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import hristostefanov.minibankingdemo.presentation.MainUiContinuation
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RetryDialog : DialogFragment() {
    private val args: RetryDialogArgs by navArgs()

    @Inject
    internal lateinit var mainUiContinuation: MainUiContinuation

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Set if can be cancelled by Back button or tapping outside of the dialog
        isCancelable = args.isCancelable

        return MaterialAlertDialogBuilder(requireContext())
            .setMessage(args.message)
            .setTitle("Retry?")
            .setPositiveButton("Retry") { _, _ ->
                lifecycleScope.launch {
                    mainUiContinuation.onConfirmRetrying()

                    // This is a must when using the navigation library
                    findNavController().popBackStack()
                }
            }
            .run {
                if (args.isCancelable) {
                    setNegativeButton(android.R.string.cancel) { _, _ ->
                        lifecycleScope.launch {
                            mainUiContinuation.onCancelRetrying()
                        }
                    }
                } else {
                    this
                }
            }
            .create()
    }
}