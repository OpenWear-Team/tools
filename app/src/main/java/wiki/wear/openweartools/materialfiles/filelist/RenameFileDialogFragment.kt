/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.filelist

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.file.FileItem
import wiki.wear.openweartools.materialfiles.util.ParcelableArgs
import wiki.wear.openweartools.materialfiles.util.args
import wiki.wear.openweartools.materialfiles.util.putArgs
import wiki.wear.openweartools.materialfiles.util.show

class RenameFileDialogFragment : FileNameDialogFragment() {
    private val args by args<Args>()

    override val listener: Listener
        get() = super.listener as Listener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        if (savedInstanceState == null) {
            val file = args.file
            binding.nameEdit.setText(file.name)
            binding.nameEdit.setSelection(0, file.baseName.length)
        }
        return dialog
    }

    @StringRes
    override val titleRes: Int = R.string.rename

    override fun isNameUnchanged(name: String): Boolean = name == args.file.name

    override fun onOk(name: String) {
        listener.renameFile(args.file, name)
    }

    companion object {
        fun show(file: FileItem, fragment: Fragment) {
            RenameFileDialogFragment().putArgs(Args(file)).show(fragment)
        }
    }

    @Parcelize
    class Args(val file: FileItem) : ParcelableArgs

    interface Listener : FileNameDialogFragment.Listener {
        fun renameFile(file: FileItem, newName: String)
    }
}
