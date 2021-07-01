/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.common

import android.os.Parcel
import java8.nio.file.attribute.FileTime
import kotlinx.parcelize.Parceler
import wiki.wear.openweartools.materialfiles.compat.readSerializableCompat

object FileTimeParceler : Parceler<FileTime?> {
    override fun create(parcel: Parcel): FileTime? = FileTime.from(parcel.readSerializableCompat())

    override fun FileTime?.write(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(this?.toInstant())
    }
}
