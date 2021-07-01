/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.terminal

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import wiki.wear.openweartools.materialfiles.util.startActivitySafe

object Terminal {
    fun open(path: String, context: Context) {
        val intent = Intent()
            .setComponent(ComponentName("jackpal.androidterm", "jackpal.androidterm.TermHere"))
            .setAction(Intent.ACTION_SEND)
            .putExtra(Intent.EXTRA_STREAM, Uri.parse(path))
        context.startActivitySafe(intent)
    }
}
