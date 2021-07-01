/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.storage

import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import java8.nio.file.Path
import kotlinx.parcelize.Parcelize
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.compat.getDescriptionCompat
import wiki.wear.openweartools.materialfiles.compat.isPrimaryCompat
import wiki.wear.openweartools.materialfiles.compat.pathCompat
import wiki.wear.openweartools.materialfiles.file.DocumentTreeUri
import wiki.wear.openweartools.materialfiles.file.displayName
import wiki.wear.openweartools.materialfiles.file.storageVolume
import wiki.wear.openweartools.materialfiles.provider.document.createDocumentTreeRootPath
import wiki.wear.openweartools.materialfiles.util.createIntent
import wiki.wear.openweartools.materialfiles.util.putArgs
import kotlin.random.Random

@Parcelize
data class DocumentTree(
    override val id: Long,
    override val customName: String?,
    val uri: DocumentTreeUri
) : Storage() {
    constructor(
        id: Long?,
        customName: String?,
        uri: DocumentTreeUri
    ) : this(id ?: Random.nextLong(), customName, uri)

    override val iconRes: Int
        @DrawableRes
        get() {
            val storageVolume = uri.storageVolume
            return if (storageVolume != null && !storageVolume.isPrimaryCompat) {
                R.drawable.sd_card_icon_white_24dp
            } else {
                super.iconRes
            }
        }

    override fun getDefaultName(context: Context): String =
        uri.storageVolume?.getDescriptionCompat(context) ?: uri.displayName ?: uri.value.toString()

    override val description: String
        get() = uri.value.toString()

    override val path: Path
        get() = uri.value.createDocumentTreeRootPath()

    override val linuxPath: String?
        get() = uri.storageVolume?.pathCompat

    override fun createEditIntent(): Intent =
        EditDocumentTreeDialogActivity::class.createIntent()
            .putArgs(EditDocumentTreeDialogFragment.Args(this))
}
