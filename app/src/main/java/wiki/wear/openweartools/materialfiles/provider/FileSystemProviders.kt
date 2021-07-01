/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider

import java8.nio.file.Files
import java8.nio.file.ProviderNotFoundException
import java8.nio.file.spi.FileSystemProvider
import wiki.wear.openweartools.materialfiles.provider.archive.ArchiveFileSystemProvider
import wiki.wear.openweartools.materialfiles.provider.common.AndroidFileTypeDetector
import wiki.wear.openweartools.materialfiles.provider.content.ContentFileSystemProvider
import wiki.wear.openweartools.materialfiles.provider.document.DocumentFileSystemProvider
import wiki.wear.openweartools.materialfiles.provider.linux.LinuxFileSystemProvider
import wiki.wear.openweartools.materialfiles.provider.root.isRunningAsRoot
import wiki.wear.openweartools.materialfiles.provider.sftp.SftpFileSystemProvider
import wiki.wear.openweartools.materialfiles.provider.smb.SmbFileSystemProvider

object FileSystemProviders {
    /**
     * If set, WatchService implementations will skip processing any event data and simply send an
     * overflow event to all the registered keys upon successful read from the inotify fd. This can
     * help reducing the JNI and GC overhead when large amount of inotify events are generated.
     * Simply sending an overflow event to all the keys is okay because we use only one key per
     * service for WatchServicePathObservable.
     */
    @Volatile
    var overflowWatchEvents = false

    fun install() {
        FileSystemProvider.installDefaultProvider(LinuxFileSystemProvider)
        FileSystemProvider.installProvider(ArchiveFileSystemProvider)
        if (!isRunningAsRoot) {
            FileSystemProvider.installProvider(ContentFileSystemProvider)
            FileSystemProvider.installProvider(DocumentFileSystemProvider)
            FileSystemProvider.installProvider(SftpFileSystemProvider)
            FileSystemProvider.installProvider(SmbFileSystemProvider)
        }
        Files.installFileTypeDetector(AndroidFileTypeDetector)
    }

    operator fun get(scheme: String): FileSystemProvider {
        for (provider in FileSystemProvider.installedProviders()) {
            if (provider.scheme.equals(scheme, ignoreCase = true)) {
                return provider
            }
        }
        throw ProviderNotFoundException(scheme)
    }
}
