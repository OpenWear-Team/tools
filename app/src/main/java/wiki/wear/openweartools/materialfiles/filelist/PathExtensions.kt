/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.filelist

import java8.nio.file.Path
import wiki.wear.openweartools.materialfiles.file.MimeType
import wiki.wear.openweartools.materialfiles.file.isSupportedArchive
import wiki.wear.openweartools.materialfiles.provider.archive.archiveFile
import wiki.wear.openweartools.materialfiles.provider.archive.isArchivePath
import wiki.wear.openweartools.materialfiles.provider.linux.isLinuxPath

val Path.name: String
    get() = fileName?.toString() ?: if (isArchivePath) archiveFile.fileName.toString() else "/"

val Path.userFriendlyString: String
    get() = if (isLinuxPath) toFile().path else toUri().toString()

fun Path.isArchiveFile(mimeType: MimeType): Boolean = isLinuxPath && mimeType.isSupportedArchive
