/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.util

import android.media.MediaMetadataRetriever
import java8.nio.file.Path
import wiki.wear.openweartools.materialfiles.provider.document.isDocumentPath
import wiki.wear.openweartools.materialfiles.provider.document.resolver.DocumentResolver
import wiki.wear.openweartools.materialfiles.provider.linux.isLinuxPath

val Path.isMediaMetadataRetrieverCompatible: Boolean
    get() = isLinuxPath || isDocumentPath

fun MediaMetadataRetriever.setDataSource(path: Path) {
    when {
        path.isLinuxPath -> setDataSource(path.toFile().path)
        path.isDocumentPath ->
            DocumentResolver.openParcelFileDescriptor(path as DocumentResolver.Path, "r")
                .use { pfd -> setDataSource(pfd.fileDescriptor) }
        else -> throw IllegalArgumentException(path.toString())
    }
}
