/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.archive

import java8.nio.file.Path
import wiki.wear.openweartools.materialfiles.provider.common.PosixFileAttributeView
import wiki.wear.openweartools.materialfiles.provider.common.PosixFileAttributes
import wiki.wear.openweartools.materialfiles.provider.root.RootPosixFileAttributeView
import java.io.IOException

internal class RootArchiveFileAttributeView(
    attributeView: PosixFileAttributeView,
    private val path: Path
) : RootPosixFileAttributeView(attributeView) {
    @Throws(IOException::class)
    override fun readAttributes(): PosixFileAttributes {
        ArchiveFileSystemProvider.doRefreshIfNeeded(path)
        return super.readAttributes()
    }
}
