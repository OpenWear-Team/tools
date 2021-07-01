/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.linux

import android.os.Parcel
import android.os.Parcelable
import wiki.wear.openweartools.materialfiles.compat.readBooleanCompat
import wiki.wear.openweartools.materialfiles.compat.writeBooleanCompat
import wiki.wear.openweartools.materialfiles.provider.root.RootPosixFileAttributeView
import wiki.wear.openweartools.materialfiles.provider.root.RootablePosixFileAttributeView
import wiki.wear.openweartools.materialfiles.util.readParcelable

internal class LinuxFileAttributeView constructor(
    private val path: LinuxPath,
    private val noFollowLinks: Boolean
) : RootablePosixFileAttributeView(
    path, LocalLinuxFileAttributeView(path.toByteString(), noFollowLinks),
    { RootPosixFileAttributeView(it) }
) {
    private constructor(source: Parcel) : this(
        source.readParcelable()!!, source.readBooleanCompat()
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(path, flags)
        dest.writeBooleanCompat(noFollowLinks)
    }

    companion object {
        val SUPPORTED_NAMES = LocalLinuxFileAttributeView.SUPPORTED_NAMES

        @JvmField
        val CREATOR = object : Parcelable.Creator<LinuxFileAttributeView> {
            override fun createFromParcel(source: Parcel): LinuxFileAttributeView =
                LinuxFileAttributeView(source)

            override fun newArray(size: Int): Array<LinuxFileAttributeView?> = arrayOfNulls(size)
        }
    }
}
