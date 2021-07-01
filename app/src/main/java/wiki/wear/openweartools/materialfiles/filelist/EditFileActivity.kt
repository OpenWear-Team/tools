/*
 * Copyright (c) 2021 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.filelist

import android.os.Bundle
import java8.nio.file.Path
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import wiki.wear.openweartools.materialfiles.app.AppActivity
import wiki.wear.openweartools.materialfiles.file.MimeType
import wiki.wear.openweartools.materialfiles.file.fileProviderUri
import wiki.wear.openweartools.materialfiles.util.ParcelableArgs
import wiki.wear.openweartools.materialfiles.util.ParcelableParceler
import wiki.wear.openweartools.materialfiles.util.args
import wiki.wear.openweartools.materialfiles.util.createEditIntent
import wiki.wear.openweartools.materialfiles.util.startActivitySafe

// Use a trampoline activity so that we can have a proper icon and title.
class EditFileActivity : AppActivity() {
    private val args by args<Args>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivitySafe(args.path.fileProviderUri.createEditIntent(args.mimeType))
        finish()
    }

    @Parcelize
    class Args(
        val path: @WriteWith<ParcelableParceler> Path,
        val mimeType: MimeType
    ) : ParcelableArgs
}
