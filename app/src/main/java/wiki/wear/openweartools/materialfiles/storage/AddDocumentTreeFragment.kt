/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.storage

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import wiki.wear.openweartools.materialfiles.file.DocumentTreeUri
import wiki.wear.openweartools.materialfiles.file.asDocumentTreeUriOrNull
import wiki.wear.openweartools.materialfiles.file.takePersistablePermission
import wiki.wear.openweartools.materialfiles.util.finish
import wiki.wear.openweartools.materialfiles.util.launchSafe

class AddDocumentTreeFragment : Fragment() {
    private val openDocumentTreeLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree(), this::onOpenDocumentTreeResult
    )

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState == null) {
            openDocumentTreeLauncher.launchSafe(null, this)
        }
    }

    private fun onOpenDocumentTreeResult(result: Uri?) {
        val documentTreeUri = result?.asDocumentTreeUriOrNull()
        if (documentTreeUri != null) {
            addDocumentTree(documentTreeUri)
        }
        finish()
    }

    private fun addDocumentTree(treeUri: DocumentTreeUri) {
        treeUri.takePersistablePermission()
        Storages.addOrReplace(DocumentTree(null, null, treeUri))
    }
}
