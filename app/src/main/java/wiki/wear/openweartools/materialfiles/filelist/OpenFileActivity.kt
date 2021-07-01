/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.filelist

import android.content.Intent
import android.os.Bundle
import java8.nio.file.Path
import wiki.wear.openweartools.materialfiles.app.AppActivity
import wiki.wear.openweartools.materialfiles.app.application
import wiki.wear.openweartools.materialfiles.file.MimeType
import wiki.wear.openweartools.materialfiles.file.asMimeTypeOrNull
import wiki.wear.openweartools.materialfiles.file.fileProviderUri
import wiki.wear.openweartools.materialfiles.filejob.FileJobService
import wiki.wear.openweartools.materialfiles.provider.archive.isArchivePath
import wiki.wear.openweartools.materialfiles.util.createViewIntent
import wiki.wear.openweartools.materialfiles.util.extraPath
import wiki.wear.openweartools.materialfiles.util.startActivitySafe

class OpenFileActivity : AppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val path = intent.extraPath
        val mimeType = intent.type?.asMimeTypeOrNull()
        if (path != null && mimeType != null) {
            openFile(path, mimeType)
        }
        finish()
    }

    private fun openFile(path: Path, mimeType: MimeType) {
        if (path.isArchivePath) {
            FileJobService.open(path, mimeType, false, this)
        } else {
            val intent = path.fileProviderUri.createViewIntent(mimeType)
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                .apply { extraPath = path }
            startActivitySafe(intent)
        }
    }

    companion object {
        private const val ACTION_OPEN_FILE = "wiki.wear.openweartools.materialfiles.intent.action.OPEN_FILE"

        fun createIntent(path: Path, mimeType: MimeType): Intent =
            Intent(ACTION_OPEN_FILE)
                .setPackage(application.packageName)
                .setType(mimeType.value)
                .apply { extraPath = path }
    }
}
