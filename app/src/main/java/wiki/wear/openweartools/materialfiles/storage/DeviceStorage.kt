/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.storage

import android.content.Context
import android.content.Intent
import android.os.storage.StorageVolume
import androidx.annotation.DrawableRes
import java8.nio.file.Path
import java8.nio.file.Paths
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.compat.getDescriptionCompat
import wiki.wear.openweartools.materialfiles.compat.isPrimaryCompat
import wiki.wear.openweartools.materialfiles.compat.pathCompat
import wiki.wear.openweartools.materialfiles.util.createIntent
import wiki.wear.openweartools.materialfiles.util.putArgs
import wiki.wear.openweartools.materialfiles.util.valueCompat

sealed class DeviceStorage : Storage() {
    override val description: String
        get() = linuxPath

    override val path: Path
        get() = Paths.get(linuxPath)

    abstract override val linuxPath: String

    override fun createEditIntent(): Intent =
        EditDeviceStorageDialogActivity::class.createIntent()
            .putArgs(EditDeviceStorageDialogFragment.Args(this))

    fun copy_(
        customName: String? = this.customName,
        isVisible: Boolean = this.isVisible
    ): DeviceStorage =
        when (this) {
            is FileSystemRoot -> copy(customName, isVisible)
            is PrimaryStorageVolume -> copy(customName, isVisible)
        }
}

@Parcelize
data class FileSystemRoot(
    override val customName: String?,
    override val isVisible: Boolean
) : DeviceStorage() {
    @IgnoredOnParcel
    override val id: Long = "FileSystemRoot".hashCode().toLong()

    @DrawableRes
    @IgnoredOnParcel
    override val iconRes: Int = R.drawable.device_icon_white_24dp

    override fun getDefaultName(context: Context): String =
        context.getString(R.string.storage_file_system_root_title)

    @IgnoredOnParcel
    override val linuxPath: String = LINUX_PATH

    companion object {
        const val LINUX_PATH = "/"
    }
}

@Parcelize
data class PrimaryStorageVolume(
    override val customName: String?,
    override val isVisible: Boolean
) : DeviceStorage() {
    @IgnoredOnParcel
    override val id: Long = "PrimaryStorageVolume".hashCode().toLong()

    @DrawableRes
    @IgnoredOnParcel
    override val iconRes: Int = R.drawable.sd_card_icon_white_24dp

    override fun getDefaultName(context: Context): String =
        storageVolume.getDescriptionCompat(context)

    override val linuxPath: String
        get() = storageVolume.pathCompat

    private val storageVolume: StorageVolume
        get() = StorageVolumeListLiveData.valueCompat.find { it.isPrimaryCompat }!!
}
