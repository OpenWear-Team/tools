/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.common

import android.os.Parcel
import android.os.Parcelable
import wiki.wear.openweartools.materialfiles.compat.readSerializableCompat
import wiki.wear.openweartools.materialfiles.util.toEnumSet
import java.io.Serializable

class ParcelablePosixFileMode(val value: Set<PosixFileModeBit>) : Parcelable {
    private constructor(source: Parcel) : this(
        source.readSerializableCompat<Set<PosixFileModeBit>>()!!
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        val serializable = when (value) {
            is Serializable -> value
            else -> value.toEnumSet()
        }
        dest.writeSerializable(serializable)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<ParcelablePosixFileMode> {
            override fun createFromParcel(source: Parcel): ParcelablePosixFileMode =
                ParcelablePosixFileMode(source)

            override fun newArray(size: Int): Array<ParcelablePosixFileMode?> = arrayOfNulls(size)
        }
    }
}

fun Set<PosixFileModeBit>.toParcelable(): ParcelablePosixFileMode = ParcelablePosixFileMode(this)
