package hristostefanov.minibankingdemo.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import hristostefanov.minibankingdemo.presentation.MainUIContinuation
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ConfirmationDialog : DialogFragment() {
    companion object Companion {
        const val TITLE_KEY = "title"
        const val MESSAGE_KEY = "message"
        const val IS_CANCELABLE = "isCancelable"

        fun create(title: String?, message: String?, isCancelable: Boolean): ConfirmationDialog {
            return ConfirmationDialog().apply {
                arguments = Bundle().apply {
                    putString(TITLE_KEY, title)
                    putString(MESSAGE_KEY, message)
                    putBoolean(IS_CANCELABLE, isCancelable)
                }
            }
        }
    }

    @Inject
    internal lateinit var mainUIContinuation: MainUIContinuation

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val isCancelableArg = checkNotNull(arguments).getBoolean(IS_CANCELABLE)
        val messageArg = checkNotNull(arguments).getString(MESSAGE_KEY)
        val titleArg = checkNotNull(arguments).getString(TITLE_KEY)

        // Controls if can be cancelled by navigating Back or tapping outside of the dialog - these are analogous to clicking the X
        // button on desktop UIs. On Android, tapping slightly outside of a dialog button can unintentionally cancel the dialog
        // so this can be disabled regardless of isCancelableArg.
        isCancelable = isCancelableArg

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(titleArg)
            .setMessage(messageArg)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                lifecycleScope.launch {
                    mainUIContinuation.onConfirm()
                }
            }
            .run {
                if (isCancelable) {
                    setNegativeButton(android.R.string.cancel) { _, _ ->
                        lifecycleScope.launch {
                            mainUIContinuation.onCancel()
                        }
                    }
                } else {
                    this
                }
            }
            .create()
    }


    override fun onCancel(dialog: DialogInterface) {
        lifecycleScope.launch {
            mainUIContinuation.onCancel()
        }
    }
}