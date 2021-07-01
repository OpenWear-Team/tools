/*
 * Copyright (c) 2021 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.compat

import android.content.pm.PermissionInfo
import androidx.core.content.pm.PermissionInfoCompat

val PermissionInfo.protectionCompat: Int
    get() = PermissionInfoCompat.getProtection(this)
