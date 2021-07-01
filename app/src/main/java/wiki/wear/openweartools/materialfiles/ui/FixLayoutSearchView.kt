/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.appcompat.widget.SearchView
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePaddingRelative
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.compat.requireViewByIdCompat
import wiki.wear.openweartools.materialfiles.util.dpToDimensionPixelSize
import wiki.wear.openweartools.materialfiles.util.getDrawableByAttr

open class FixLayoutSearchView : SearchView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    init {
        // A negative value won't work here because SearchView will use its preferred width as max
        // width instead.
        maxWidth = Int.MAX_VALUE
        val searchEditFrame = requireViewByIdCompat<View>(R.id.search_edit_frame)
        searchEditFrame.updateLayoutParams<MarginLayoutParams> {
            leftMargin = 0
            rightMargin = 0
        }
        val searchSrcText = requireViewByIdCompat<View>(R.id.search_src_text)
        searchSrcText.updatePaddingRelative(start = 0, end = 0)
        val searchCloseBtn = requireViewByIdCompat<View>(R.id.search_close_btn)
        val searchCloseBtnPaddingHorizontal = searchCloseBtn.context.dpToDimensionPixelSize(12)
        searchCloseBtn.updatePaddingRelative(
            start = searchCloseBtnPaddingHorizontal, end = searchCloseBtnPaddingHorizontal
        )
        searchCloseBtn.background = searchCloseBtn.context
            .getDrawableByAttr(R.attr.actionBarItemBackground)
    }
}
