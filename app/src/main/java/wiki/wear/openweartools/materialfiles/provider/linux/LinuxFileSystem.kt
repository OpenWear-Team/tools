/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.linux

import android.os.Parcel
import android.os.Parcelable
import wiki.wear.openweartools.materialfiles.provider.common.ByteString
import wiki.wear.openweartools.materialfiles.provider.common.ByteStringListPathCreator
import wiki.wear.openweartools.materialfiles.provider.root.RootFileSystem
import wiki.wear.openweartools.materialfiles.provider.root.RootableFileSystem

internal class LinuxFileSystem(provider: LinuxFileSystemProvider) : RootableFileSystem(
    { LocalLinuxFileSystem(it as LinuxFileSystem, provider) }, { RootFileSystem(it) }
), ByteStringListPathCreator {
    override val localFileSystem: LocalLinuxFileSystem
        get() = super.localFileSystem as LocalLinuxFileSystem

    val rootDirectory: LinuxPath
        get() = localFileSystem.rootDirectory

    val defaultDirectory: LinuxPath
        get() = localFileSystem.defaultDirectory

    override fun getPath(first: ByteString, vararg more: ByteString): LinuxPath =
        localFileSystem.getPath(first, *more)

    override fun close() {
        throw UnsupportedOperationException()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {}

    companion object {
        const val SEPARATOR = LocalLinuxFileSystem.SEPARATOR

        @JvmField
        val CREATOR = object : Parcelable.Creator<LinuxFileSystem> {
            override fun createFromParcel(source: Parcel): LinuxFileSystem =
                LinuxFileSystemProvider.fileSystem

            override fun newArray(size: Int): Array<LinuxFileSystem?> = arrayOfNulls(size)
        }
    }
}
