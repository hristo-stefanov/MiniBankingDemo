package hristostefanov.starlingdemo.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import hristostefanov.starlingdemo.R

class ErrorDialog: DialogFragment() {
    private val args: ErrorDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setMessage(args.message)
            .setTitle(getString(R.string.error_dialog_title))
            .setPositiveButton(android.R.string.ok) {_, _ -> }
            .create()
    }
}