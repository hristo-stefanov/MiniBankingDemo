package hristostefanov.minibankingdemo.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hristostefanov.minibankingdemo.R

class ErrorDialog: DialogFragment() {
    private val args: ErrorDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setMessage(args.message)
            .setTitle(getString(R.string.error_dialog_title))
            .setPositiveButton(android.R.string.ok) {_, _ -> }
            .create()
    }
}