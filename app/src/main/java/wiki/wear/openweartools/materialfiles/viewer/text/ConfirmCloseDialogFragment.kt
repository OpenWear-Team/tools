/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.viewer.text

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.util.show

class ConfirmCloseDialogFragment : AppCompatDialogFragment() {
    private val listener: Listener
        get() = requireParentFragment() as Listener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), theme)
            .setMessage(R.string.text_editor_close_message)
            .setPositiveButton(R.string.keep_editing, null)
            .setNegativeButton(R.string.discard) { _, _ -> listener.finish() }
            .create()
    }

    companion object {
        fun show(fragment: Fragment) {
            ConfirmCloseDialogFragment().show(fragment)
        }
    }

    interface Listener {
        fun finish()
    }
}
