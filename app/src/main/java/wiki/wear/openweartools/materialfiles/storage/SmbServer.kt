/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.storage

import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import java8.nio.file.Path
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.provider.smb.client.Authentication
import wiki.wear.openweartools.materialfiles.provider.smb.client.Authority
import wiki.wear.openweartools.materialfiles.provider.smb.createSmbRootPath
import wiki.wear.openweartools.materialfiles.util.createIntent
import wiki.wear.openweartools.materialfiles.util.putArgs
import kotlin.random.Random

@Parcelize
class SmbServer(
    override val id: Long,
    override val customName: String?,
    val authority: Authority,
    val authentication: Authentication,
    val relativePath: String
) : Storage() {
    constructor(
        id: Long?,
        customName: String?,
        authority: Authority,
        authentication: Authentication,
        relativePath: String
    ) : this(id ?: Random.nextLong(), customName, authority, authentication, relativePath)

    @DrawableRes
    @IgnoredOnParcel
    override val iconRes: Int = R.drawable.computer_icon_white_24dp

    override fun getDefaultName(context: Context): String =
        if (relativePath.isNotEmpty()) "$authority/$relativePath" else authority.toString()

    override val description: String
        get() = authority.toString()

    override val path: Path
        get() = authority.createSmbRootPath().resolve(relativePath)

    override fun createEditIntent(): Intent =
        EditSmbServerActivity::class.createIntent().putArgs(EditSmbServerFragment.Args(this))
}
