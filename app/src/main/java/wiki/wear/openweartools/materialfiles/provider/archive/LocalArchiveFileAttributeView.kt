/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.archive

import java8.nio.file.Path
import java8.nio.file.attribute.FileTime
import wiki.wear.openweartools.materialfiles.provider.common.ByteString
import wiki.wear.openweartools.materialfiles.provider.common.PosixFileAttributeView
import wiki.wear.openweartools.materialfiles.provider.common.PosixFileModeBit
import wiki.wear.openweartools.materialfiles.provider.common.PosixGroup
import wiki.wear.openweartools.materialfiles.provider.common.PosixUser
import java.io.IOException

internal class LocalArchiveFileAttributeView(private val path: Path) : PosixFileAttributeView {
    override fun name(): String = NAME

    @Throws(IOException::class)
    override fun readAttributes(): ArchiveFileAttributes {
        val fileSystem = path.fileSystem as ArchiveFileSystem
        val entry = fileSystem.getEntryAsLocal(path)
        return ArchiveFileAttributes.from(fileSystem.archiveFile, entry)
    }

    override fun setTimes(
        lastModifiedTime: FileTime?,
        lastAccessTime: FileTime?,
        createTime: FileTime?
    ) {
        throw UnsupportedOperationException()
    }

    override fun setOwner(owner: PosixUser) {
        throw UnsupportedOperationException()
    }

    override fun setGroup(group: PosixGroup) {
        throw UnsupportedOperationException()
    }

    override fun setMode(mode: Set<PosixFileModeBit>) {
        throw UnsupportedOperationException()
    }

    override fun setSeLinuxContext(context: ByteString) {
        throw UnsupportedOperationException()
    }

    override fun restoreSeLinuxContext() {
        throw UnsupportedOperationException()
    }

    companion object {
        private val NAME = ArchiveFileSystemProvider.scheme

        val SUPPORTED_NAMES = setOf("basic", "posix", NAME)
    }
}
