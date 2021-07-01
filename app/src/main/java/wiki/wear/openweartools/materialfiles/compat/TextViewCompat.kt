/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.compat

import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.core.widget.TextViewCompat

fun TextView.setTextAppearanceCompat(@StyleRes resId: Int) {
    TextViewCompat.setTextAppearance(this, resId)
}
