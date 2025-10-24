package hristostefanov.minibankingdemo.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hristostefanov.minibankingdemo.presentation.MainViewModel
import kotlinx.coroutines.launch
import kotlin.coroutines.resume

class RetryDialog : DialogFragment() {
    private val args: RetryDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Set if can be cancelled by Back button or tapping outside of the dialog
        isCancelable = args.isCancelable

        return MaterialAlertDialogBuilder(requireContext())
            .setMessage(args.message)
            .setTitle("Retry?")
            .setPositiveButton("Retry") { _, _ ->
                val mainViewModel by activityViewModels<MainViewModel>()
                lifecycleScope.launch {
                    mainViewModel.userInterfaceImpl.retryRecoveryContinuation.resume(true)

                    // This is a must when using the navigation library
                    findNavController().popBackStack()
                }
            }
            .run {
                if (args.isCancelable) {
                    setNegativeButton(android.R.string.cancel) { _, _ ->
                        val mainViewModel by activityViewModels<MainViewModel>()
                        lifecycleScope.launch {
                            mainViewModel.userInterfaceImpl.retryRecoveryContinuation.resume(false)
                        }
                    }
                } else {
                    this
                }
            }
            .create()
    }
}