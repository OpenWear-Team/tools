/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.ui

import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import wiki.wear.openweartools.materialfiles.util.fadeInUnsafe
import wiki.wear.openweartools.materialfiles.util.fadeOutUnsafe

class OverlayToolbarActionMode(bar: ViewGroup, toolbar: Toolbar) : ToolbarActionMode(bar, toolbar) {
    constructor(toolbar: Toolbar) : this(toolbar, toolbar)

    init {
        bar.isVisible = false
    }

    override fun show(bar: ViewGroup, animate: Boolean) {
        if (animate) {
            bar.fadeInUnsafe()
        } else {
            bar.isVisible = true
        }
    }

    override fun hide(bar: ViewGroup, animate: Boolean) {
        if (animate) {
            bar.fadeOutUnsafe()
        } else {
            bar.isVisible = false
        }
    }
}
