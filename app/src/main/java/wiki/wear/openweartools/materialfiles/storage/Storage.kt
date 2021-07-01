/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.storage

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.annotation.DrawableRes
import java8.nio.file.Path
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.util.takeIfNotEmpty

abstract class Storage : Parcelable {
    abstract val id: Long

    @DrawableRes
    open val iconRes: Int = R.drawable.directory_icon_white_24dp

    abstract val customName: String?

    abstract fun getDefaultName(context: Context): String

    fun getName(context: Context): String = customName?.takeIfNotEmpty() ?: getDefaultName(context)

    abstract val description: String

    abstract val path: Path

    open val linuxPath: String? = null

    open val isVisible: Boolean = true

    abstract fun createEditIntent(): Intent
}
