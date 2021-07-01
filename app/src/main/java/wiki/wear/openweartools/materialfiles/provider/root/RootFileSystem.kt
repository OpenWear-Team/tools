/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.root

import java8.nio.file.FileStore
import java8.nio.file.FileSystem
import java8.nio.file.Path
import java8.nio.file.PathMatcher
import java8.nio.file.WatchService
import java8.nio.file.attribute.UserPrincipalLookupService
import java8.nio.file.spi.FileSystemProvider
import wiki.wear.openweartools.materialfiles.provider.remote.RemoteFileSystem
import wiki.wear.openweartools.materialfiles.provider.remote.RemoteInterface
import java.io.IOException

open class RootFileSystem(fileSystem: FileSystem) : RemoteFileSystem(
    RemoteInterface { RootFileService.getRemoteFileSystemInterface(fileSystem) }
) {
    override fun provider(): FileSystemProvider {
        throw AssertionError()
    }

    override fun isOpen(): Boolean {
        throw AssertionError()
    }

    override fun isReadOnly(): Boolean {
        throw AssertionError()
    }

    override fun getSeparator(): String {
        throw AssertionError()
    }

    override fun getRootDirectories(): Iterable<Path> {
        throw AssertionError()
    }

    override fun getFileStores(): Iterable<FileStore> {
        throw AssertionError()
    }

    override fun supportedFileAttributeViews(): Set<String> {
        throw AssertionError()
    }

    override fun getPath(first: String, vararg more: String): Path {
        throw AssertionError()
    }

    override fun getPathMatcher(syntaxAndPattern: String): PathMatcher {
        throw AssertionError()
    }

    override fun getUserPrincipalLookupService(): UserPrincipalLookupService {
        throw AssertionError()
    }

    @Throws(IOException::class)
    override fun newWatchService(): WatchService {
        throw UnsupportedOperationException()
    }
}
