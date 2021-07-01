/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.storage

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.util.createIntent
import wiki.wear.openweartools.materialfiles.util.finish
import wiki.wear.openweartools.materialfiles.util.putArgs
import wiki.wear.openweartools.materialfiles.util.startActivitySafe

class AddStorageDialogFragment : AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext(), theme)
            .setTitle(R.string.storage_add_storage_title)
            .apply {
                val items = STORAGE_TYPES.map { getString(it.first) }.toTypedArray<CharSequence>()
                setItems(items) { _, which ->
                    startActivitySafe(STORAGE_TYPES[which].second)
                    dismiss()
                }
            }
            .create()

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        finish()
    }

    companion object {
        private val STORAGE_TYPES = listOf(
            R.string.storage_add_storage_document_tree
                to AddDocumentTreeActivity::class.createIntent(),
            R.string.storage_add_storage_sftp_server to EditSftpServerActivity::class.createIntent()
                .putArgs(EditSftpServerFragment.Args()),
            R.string.storage_add_storage_smb_server to AddLanSmbServerActivity::class.createIntent()
        )
    }
}
