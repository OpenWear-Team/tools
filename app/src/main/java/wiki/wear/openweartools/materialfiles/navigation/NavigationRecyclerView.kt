/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.navigation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.WindowInsets
import androidx.annotation.AttrRes
import androidx.core.graphics.withSave
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.util.activity
import wiki.wear.openweartools.materialfiles.util.displayWidth
import wiki.wear.openweartools.materialfiles.util.getDimension
import wiki.wear.openweartools.materialfiles.util.getDimensionPixelSize
import wiki.wear.openweartools.materialfiles.util.getDimensionPixelSizeByAttr
import wiki.wear.openweartools.materialfiles.util.getDrawableByAttr

class NavigationRecyclerView : RecyclerView {
    private val verticalPadding =
        context.getDimensionPixelSize(R.dimen.design_navigation_padding_bottom)
    private val actionBarSize = context.getDimensionPixelSizeByAttr(R.attr.actionBarSize)
    private val maxWidth = context.getDimensionPixelSize(R.dimen.navigation_max_width)
    private var scrim = context.getDrawableByAttr(android.R.attr.statusBarColor)

    private var insetTop = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(
        context, attrs, defStyleAttr)

    init {
        val context = context
        updatePadding(top = verticalPadding, bottom = verticalPadding)
        elevation = context.getDimension(R.dimen.design_navigation_elevation)
        fitsSystemWindows = true
        setWillNotDraw(false)
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        var widthSpec = widthSpec
        var maxWidth = (context.displayWidth - actionBarSize).coerceIn(0..maxWidth)
        when (MeasureSpec.getMode(widthSpec)) {
            MeasureSpec.AT_MOST -> {
                maxWidth = maxWidth.coerceAtMost(MeasureSpec.getSize(widthSpec))
                widthSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY)
            }
            MeasureSpec.UNSPECIFIED ->
                widthSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY)
            MeasureSpec.EXACTLY -> {}
        }
        super.onMeasure(widthSpec, heightSpec)
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        insetTop = insets.systemWindowInsetTop
        updatePadding(
            top = verticalPadding + insetTop,
            bottom = verticalPadding + insets.systemWindowInsetBottom
        )
        return insets.replaceSystemWindowInsets(
            insets.systemWindowInsetLeft, 0,
            insets.systemWindowInsetRight, 0
        )
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        if (context.activity!!.window.statusBarColor == Color.TRANSPARENT) {
            canvas.withSave {
                canvas.translate(scrollX.toFloat(), scrollY.toFloat())
                scrim.setBounds(0, 0, width, insetTop)
                scrim.draw(canvas)
            }
        }
    }
}
