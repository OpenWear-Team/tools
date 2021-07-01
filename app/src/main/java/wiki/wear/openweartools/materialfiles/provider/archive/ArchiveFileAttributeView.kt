/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.archive

import android.os.Parcel
import android.os.Parcelable
import wiki.wear.openweartools.materialfiles.provider.root.RootablePosixFileAttributeView
import wiki.wear.openweartools.materialfiles.util.readParcelable

internal class ArchiveFileAttributeView(
    private val path: ArchivePath
) : RootablePosixFileAttributeView(
    path, LocalArchiveFileAttributeView(path), { RootArchiveFileAttributeView(it, path) }
) {
    private constructor(source: Parcel) : this(source.readParcelable<ArchivePath>()!!)

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(path as Parcelable, flags)
    }

    companion object {
        val SUPPORTED_NAMES = LocalArchiveFileAttributeView.SUPPORTED_NAMES

        @JvmField
        val CREATOR = object : Parcelable.Creator<ArchiveFileAttributeView> {
            override fun createFromParcel(source: Parcel): ArchiveFileAttributeView =
                ArchiveFileAttributeView(source)

            override fun newArray(size: Int): Array<ArchiveFileAttributeView?> = arrayOfNulls(size)
        }
    }
}
